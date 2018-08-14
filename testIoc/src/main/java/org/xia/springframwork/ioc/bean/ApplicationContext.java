package org.xia.springframwork.ioc.bean;

import org.xia.springframwork.ioc.factory.BeanFactory;
import org.xia.springframwork.ioc.xml.BeanXMLReaderUtil;

/**
 * Created by sgl on 18/8/8.
 */
public abstract class ApplicationContext extends BeanFactory{
    protected String configLocation;
    protected BeanXMLReaderUtil beanXMLReaderUtil=null;

    public ApplicationContext(){
    }

    public ApplicationContext(String configLocation){
        this.configLocation=configLocation;
       // beanXMLReaderUtil=new BeanXMLReaderUtil();
    }


    protected abstract Object getBean(String beanName,Class<?>cl);
}
