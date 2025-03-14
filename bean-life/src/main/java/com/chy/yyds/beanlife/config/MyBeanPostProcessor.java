package com.chy.yyds.beanlife.config;

import com.chy.yyds.beanlife.service.TestService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestService){
            System.out.println("6 BeanPostProcessor.postProcessBeforeInitialization()");

        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestService) {
            System.out.println("10 BeanPostProcessor.postProcessAfterInitialization()");
        }
        return bean;
    }
}
