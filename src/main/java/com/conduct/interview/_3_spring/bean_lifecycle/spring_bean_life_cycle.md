Spring bean lifecycle is a set of phases which every spring bean is going through from the moment of it's
creation till the disposal. With help of intruding into those phases it's possible to set up the dependencies,
create and release some resources, integrate some external none-spring libraries into Spring etc. It consists of:

- Instantiation (constructor execution)
- Populating of properties (calling setters)
- Aware hooks (BeanNameAware, BeanFactoryAware, ApplicationContextAware)
- BeanPostProcessor with `postProcessBeforeInit` and `postProcessAfterInit`
- PostConstruct annotation which is executed between upper postProcess methods
- InitializingBean with `afterPropertiesSet` method
- CustomInitMethod
- PreDestroy Annotation
- DisposableBean with `destory` method
- Custom destroy method