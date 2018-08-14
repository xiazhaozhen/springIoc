package org.xia.springframwork.ioc.factory;

/**
 * 容器对象工程
 */
public  abstract class BeanFactory {
    public Object getBean(String beanName){
        return doGetBean(beanName);
    }

    protected abstract Object doGetBean(String beanName);
}
