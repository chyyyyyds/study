package com.chy.springmvc.aop;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("Filter 初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("进入 MyFilter");
        chain.doFilter(request, response); // 放行
        System.out.println("离开 MyFilter");
    }

    @Override
    public void destroy() {
        System.out.println("Filter 销毁");
    }
}
