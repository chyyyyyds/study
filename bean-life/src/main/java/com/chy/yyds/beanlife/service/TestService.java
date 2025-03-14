package com.chy.yyds.beanlife.service;

import com.chy.yyds.beanlife.config.MyBeanPostProcessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TestService implements InitializingBean, BeanNameAware, BeanFactoryAware, ApplicationContextAware, DisposableBean {


    private MyBeanPostProcessor myBeanPostProcessor;


    @Autowired
    public void setMyBeanPostProcessor(MyBeanPostProcessor myBeanPostProcessor) {
        System.out.println("2 set 属性赋值");
        this.myBeanPostProcessor = myBeanPostProcessor;
    }

    public TestService() {
        System.out.println("1 constructor 构造方法" );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("8 afterPropertiesSet InitializingBean的afterPropertiesSet()");
    }

    public void init(){
        System.out.println("9 init()");
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("7 PostConstruct 注解");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("3 aware beanNameAware");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("4 aware BeanFactoryAware");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("5 aware ApplicationContextAware");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("11 destroy DisposableBean的destroy()");
    }
}
