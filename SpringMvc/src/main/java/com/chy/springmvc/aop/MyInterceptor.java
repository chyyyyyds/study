package com.chy.springmvc.aop;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MyInterceptor implements HandlerInterceptor {

    // 请求前调用（true 继续执行，false 不继续）
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod){
            System.out.println("【拦截器】请求方法：" + ((HandlerMethod) handler).getMethod().getName());
        }
        System.out.println("【拦截器】请求路径：" + request.getRequestURI());
        return true;
    }

    // 请求处理完之后调用（Controller 方法执行完）
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        System.out.println("【拦截器】Controller 方法调用结束");
    }

    // 整个请求结束后调用（包括视图渲染）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        System.out.println("【拦截器】请求完成");
    }
}
