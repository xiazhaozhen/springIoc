package org.xia.springframwork.ioc.bean.annotation;

import org.xia.springframwork.ioc.bean.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
/**
 * Created by sgl on 18/8/8.
 */
public class AnnotationApplicationContext extends ApplicationContext {

    //需要扫描类的路径的缓存
    private List<Class<?>> classCache= Collections.synchronizedList(new ArrayList<Class<?>>(128));

    //bean 容器
    private Map<String,Object> beanDefinations=new ConcurrentHashMap<>(256);

    public AnnotationApplicationContext(String configLocation)throws Exception{
        super(configLocation);
        //获取包的路径
        String packagePath=beanXMLReaderUtil.handerXMLForScanPackage(configLocation);
        //执行包的扫描操作
        scanPackage(packagePath);
        //执行容器的管理类的实例化
        doCreateBean();
        // 容器的依赖装配
        pouplateBean();

    }


     private void pouplateBean(){

     }
    /**
     * IOC 操作
     */
    private void doCreateBean(){

    }
    /**
     * 包扫描
     * @param packagePath
     */
    private void scanPackage(String packagePath){

    }
    @Override
    protected  Object getBean(String beanName,Class<?>cl){
        return this.beanDefinations.get(beanName);
    }

    /**
     * 重容器获取实例的方法
     * @param beanName
     * @return
     */
    @Override
    protected Object doGetBean(String beanName){
        return this.beanDefinations.get(beanName);
    }


}
