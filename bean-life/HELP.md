## Bean的生命周期

```azure
1 constructor 构造方法
2 set 属性赋值
3 aware beanNameAware
4 aware BeanFactoryAware
5 aware ApplicationContextAware
6 BeanPostProcessor.postProcessBeforeInitialization()
7 PostConstruct 注解
8 afterPropertiesSet InitializingBean的afterPropertiesSet()
9 init()
10 BeanPostProcessor.postProcessAfterInitialization()
11 destroy DisposableBean的destroy()
```
bean的生命周期可分为以下阶段：
1. 实例化: 调用构造方法把对象new出来（如果使用有参构造，解决不了循环依赖问题)
2. 属性赋值：对应源码的populateBean方法  为属性赋值 调用set方法
3. 初始化：这个阶段有很多回调接口：
   4. aware接口：BeanNameAware、BeanFactoryAware、ApplicationContextAware
   5. BeanPostProcessor的before
   6. PostConstruct
   7. InitializingBean的afterPropertiesSet()
   8. inti()
   9. BeanPostProcessor的after
4. 使用
5. 销毁,回调DisposableBean的destroy方法
 