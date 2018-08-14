package org.xia.springframwork.ioc.context;

/**
 * Created by sgl on 18/8/13.
 */
public abstract class ApplicationContext {
    public Object getBean(String beanName){
        return doGetBean(beanName);
    }
    protected abstract Object doGetBean(String beanName);
}
