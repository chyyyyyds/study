# DispatcherServlet的doDispatch方法
```java
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null; 
                                                   
		boolean multipartRequestParsed = false;

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

		try {
			ModelAndView mv = null;
			Exception dispatchException = null;

			try {
				processedRequest = checkMultipart(request);
				multipartRequestParsed = (processedRequest != request);

				mappedHandler = getHandler(processedRequest);
				if (mappedHandler == null) {
					noHandlerFound(processedRequest, response);
					return;
				}

				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

				String method = request.getMethod();
				boolean isGet = "GET".equals(method);
				if (isGet || "HEAD".equals(method)) {
					long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
					if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
						return; 
					}
				}

				if (!mappedHandler.applyPreHandle(processedRequest, response)) {   
					return;
				}

				mv = ha.handle(processedRequest, response, mappedHandler.getHandler()); 

				if (asyncManager.isConcurrentHandlingStarted()) {
					return;
				}

				applyDefaultViewName(processedRequest, mv);
				mappedHandler.applyPostHandle(processedRequest, response, mv);
			}
			catch (Exception ex) {
				dispatchException = ex;
			}
			catch (Throwable err) {
				dispatchException = new NestedServletException("Handler dispatch failed", err);
			}
			processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException); 
		}
		catch (Exception ex) {
			triggerAfterCompletion(processedRequest, response, mappedHandler, ex); 
		}
		catch (Throwable err) {
			triggerAfterCompletion(processedRequest, response, mappedHandler,
					new NestedServletException("Handler processing failed", err));
		}
		finally {
			if (asyncManager.isConcurrentHandlingStarted()) {
				// Instead of postHandle and afterCompletion
				if (mappedHandler != null) {
					mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
				}
			}
			else {
				// Clean up any resources used by a multipart request.
				if (multipartRequestParsed) {
					cleanupMultipart(processedRequest);
				}
			}
		}
	}
```
### HandlerExecutionChain
看源码
```java
public class HandlerExecutionChain {
	private final Object handler; //这个handler 一般是 HandlerMethod
	private HandlerInterceptor[] interceptors;
	private List<HandlerInterceptor> interceptorList;
	private int interceptorIndex = -1;  //执行的拦截器索引
    private List<HandlerMapping> handlerMappings;

    @Nullable
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (HandlerMapping mapping : this.handlerMappings) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }
    
}
```
-  final Object handler：
他的`Object handler`属性，一般是HandlerMethod，如现在主流的@Controller + @RequestMapping 但不一定都是 下面会提到
-  getHandler()
getHandler方法返回的是HandlerExecutionChain，springMVC自带了几个HandlerMapping，如：
    - SimpleUrlHandlerMapping  
      - 匹配静态资源（比如 /static/、/webjars/、favicon.ico 等）
      - 返回的的HandlerExecutionChain的`Object handler`属性是`ResourceHttpRequestHandler`
    - RequestMappingHandlerMapping 
      - 现在主流的@Controller + @RequestMapping用的都是这个HandlerMapping
      - 返回的的HandlerExecutionChain的`Object handler`属性是`HandlerMethod`
    - BeanNameUrlHandlerMapping 
      - 如Spring 容器里是有一个名字叫 "myController" 的 Bean（必须实现 HttpRequestHandler 或 Controller 接口）
      - 返回的的HandlerExecutionChain的`Object handler`属性是`HttpRequestHandler`


### HandlerAdapter

- RequestMappingHandlerAdapter
  - 处理@Controller + @RequestMapping  既调用HandlerMethod
- HttpRequestHandlerAdapter
  - 处理HttpRequestHandler 
- SimpleControllerHandlerAdapter
  - 处理访问实现Spring Controller接口的类的请求 

### lastModified
缓存机制 如果是get请求且最后修改时间戳和请求头中If-Modified-Since一致，则返回304

### applyPreHandle
```java
	boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = 0; i < interceptors.length; i++) {
				HandlerInterceptor interceptor = interceptors[i];
				if (!interceptor.preHandle(request, response, this.handler)) {
					triggerAfterCompletion(request, response, null);
					return false;
				}
				this.interceptorIndex = i;
			}
		}
		return true;
	}
```
这个方法会遍历拦截器执行preHandle方法
- 如果拦截器返回true，没事发生；
- 如果任意拦截器返回false，或者抛出异常
  - 不会执行后续的HandlerMethod（既controller方法、文件下载、视图渲染）
  - 会执行所有拦截器的postHandle方法
  - 仅已经执行了preHandle的拦截器，afterCompletion方法会执行
- triggerAfterCompletion()方法也会遍历拦截器链，但是他是从this.interceptorIndex开始，反向遍历的 如123三个拦截器，2返回false，会去执行2、1的AfterCompletion
- this.interceptorIndex = i  存了成功执行preHandle的拦截器索引


### mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
restController的时候mappedHandler.getHandler()返回的就是HandleMethod，ha.handle是真正调用controller的地方。


### applyPostHandle
```java
	void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv)
        throws Exception {

    HandlerInterceptor[] interceptors = getInterceptors();
    if (!ObjectUtils.isEmpty(interceptors)) {
        for (int i = interceptors.length - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptors[i];
            interceptor.postHandle(request, response, this.handler, mv);
        }
    }
}
```
很简单 就是反向遍历拦截器调用postHandle方法
### processDispatchResult
他是在调用ha.handle() 发生异常且被catch后被调用的，也就是肯定会被调用，会触发afterCompletion方法，兜底
```java
		if (mappedHandler != null) {
			mappedHandler.triggerAfterCompletion(request, response, null);
		}
```


### triggerAfterCompletion
```java
	void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex)
			throws Exception {

		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = this.interceptorIndex; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptors[i];
				try {
					interceptor.afterCompletion(request, response, this.handler, ex);
				}
				catch (Throwable ex2) {
					logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
				}
			}
		}
	}
```
从this.interceptorIndex开始，反向遍历拦截器调用afterCompletion方法