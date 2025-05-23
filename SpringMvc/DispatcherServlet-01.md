# 认识一下 DispatcherServlet

先上代码
```java
public class DispatcherServlet extends FrameworkServlet { //间接继承了间接
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.parentApplicationContext =
                (WebApplicationContext) this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        this.sContextConfigLocation = config.getInitParameter("contextConfigLocation");

        this.webApplicationContext = new AnnotationConfigWebApplicationContext(this.sContextConfigLocation, this.parentApplicationContext);

        Refresh();

    }

    protected void Refresh() {
        initHandlerMappings(this.webApplicationContext);
        initHandlerAdapters(this.webApplicationContext);
        initViewResolvers(this.webApplicationContext);
    }
}
```
## 了解一下DispatcherServlet相关的几个类
首先 继承DispatcherServlet间接继承了HttpServlet，重写了他的init()方法，在init方法中初始化了几个主要对象：
- HandlerMapping  用来找 handler（处理器）对象 的
- HandlerAdapter  用来执行 handler 的
- ViewResolver    用来渲染视图的

这个init()方法啥时被回调的？ 默认是第一个request进来的时候调的。但是可以配置`<load-on-startup>1</load-on-startup> `让容器启动就初始化


再看看refresh方法里面初始化的东西都是啥
### HandlerMapping
其实主要就是两个方法
- initMappings：遍历controller，将controller的方法映射到mappingRegistry这个对象里面存着
- getHandler：根据request的path来获取controller的method，返回的是HandlerMethod 处理的方法 这个对象封装了这次请求要调用的方法的所有信息
```java
protected void initMappings() throws Exception {
    Class<?> clz = null;
    Object obj = null;
    String[] controllerNames = this.applicationContext.getBeanDefinitionNames();
    for (String controllerName : controllerNames) {
        clz = Class.forName(controllerName);
        obj = this.applicationContext.getBean(controllerName);

        Method[] methods = clz.getDeclaredMethods();
        if (methods != null) {
            for (Method method : methods) {
                boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                if (isRequestMapping) {
                    String methodName = method.getName();
                    String urlmapping = method.getAnnotation(RequestMapping.class).value();
                    this.mappingRegistry.getUrlMappingNames().add(urlmapping); //path
                    this.mappingRegistry.getMappingObjs().put(urlmapping, obj);//path - controllerObj
                    this.mappingRegistry.getMappingMethods().put(urlmapping, method);//path - controllerMethod
                    this.mappingRegistry.getMappingMethodNames().put(urlmapping, methodName);//path - controllerMethodName
                    this.mappingRegistry.getMappingClasses().put(urlmapping, clz);//path - controllerClass
                }
            }
        }
    }

}

@Override
public HandlerMethod getHandler(HttpServletRequest request) throws Exception {
    if (this.mappingRegistry == null) { //to do initialization
        this.mappingRegistry = new MappingRegistry();
        initMappings();
    }

    String sPath = request.getServletPath();

    if (!this.mappingRegistry.getUrlMappingNames().contains(sPath)) {
        return null;
    }

    Method method = this.mappingRegistry.getMappingMethods().get(sPath);
    Object obj = this.mappingRegistry.getMappingObjs().get(sPath);
    Class<?> clz = this.mappingRegistry.getMappingClasses().get(sPath);
    String methodName = this.mappingRegistry.getMappingMethodNames().get(sPath);

    HandlerMethod handlerMethod = new HandlerMethod(method, obj, clz, methodName);

    return handlerMethod;
}
```

### HandlerAdapter
他更简单 就是调用HandlerMethod的handle方法返回ModelAndView
```java
	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return handleInternal(request, response, (HandlerMethod) handler);
	}

	private ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handler) {
		ModelAndView mv = null;
		
		try {
			 mv = invokeHandlerMethod(request, response, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;

	}
```

### ViewResolver
ViewResolver 是用来根据 控制器返回的逻辑视图名（如 "home"）找到实际的视图文件（如 /WEB-INF/views/home.jsp）的。
举个例子：（这不是个RestController）
```java
@RequestMapping("/home")
public String home() {
    return "index";
}
```
这个 "index" 是一个 逻辑视图名，不是文件路径。
然后 ViewResolver（如 InternalResourceViewResolver）会按照配置
```xml
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/" />
    <property name="suffix" value=".jsp" />
</bean>
```
拼接成了 /WEB-INF/views/index.jsp
然后由 Servlet 容器（如 Tomcat）去加载并渲染这个 JSP。 现在谁还在啊用jsp？



捋一下 请求进到 DispatcherServlet 后的流程
```java
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerMethod handlerMethod = null;
		ModelAndView mv = null;
		
		handlerMethod = this.handlerMapping.getHandler(processedRequest);
		if (handlerMethod == null) {
			return;
		}
		
		HandlerAdapter ha = this.handlerAdapter;

		mv = ha.handle(processedRequest, response, handlerMethod);

		render(processedRequest, response, mv);
	}
```