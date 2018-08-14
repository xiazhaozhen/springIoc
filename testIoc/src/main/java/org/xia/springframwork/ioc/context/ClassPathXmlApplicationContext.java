package org.xia.springframwork.ioc.context;

import org.xia.springframwork.ioc.annotation.XiaAutowired;
import org.xia.springframwork.ioc.annotation.XiaRepository;
import org.xia.springframwork.ioc.annotation.XiaService;
import org.xia.springframwork.ioc.xml.BeanXMLReaderUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sgl on 18/8/13.
 */
public class ClassPathXmlApplicationContext extends ApplicationContext {

    private Map<String,Object> beanDefinationsMap=new ConcurrentHashMap<>();

    //定义
     List<Class<?>> classCache= Collections.synchronizedList(new ArrayList<Class<?>>());

    private String configLocation;
    public ClassPathXmlApplicationContext() {
    }


    public ClassPathXmlApplicationContext(String configLocation)throws Exception {
        this.configLocation=configLocation;
        //定义初始化容器
        onRefresh();
    }

    private void onRefresh()throws Exception{
        //获取到包扫描路径
        String packageString= BeanXMLReaderUtil.getInstance().handerXMLForScanPackage(configLocation);
        //获取class对象
        scannerPackage(packageString);
        //实例化1 定义别名 2 没定义别名
        doCreateBean();
        //管理对象的实例化操作
        pluplatebean();
    }

    //扫描
    public void scannerPackage(String packageString)throws Exception{
        URL url=this.getClass().getClassLoader().getResource(packageString.replaceAll("\\.","/"));
        File file=new File(url.getFile());
        for(File child:file.listFiles()){
            if(child.isDirectory()){
                scannerPackage(packageString+"."+child.getName());
            }else {
                System.out.println(child.getName());
                if(child.getName().endsWith(".class")){
                    System.out.println(child.getName());
                    String classPath=packageString+"."+child.getName().replaceAll("\\.class","");
                    //类路径 转换类路径
                    Class<?>loadClass=Class.forName(classPath);
//                   Class<?> loadClass= this.getClass().getClassLoader().loadClass(classPath);
                    if(loadClass.isAnnotationPresent(XiaService.class)||loadClass.isAnnotationPresent(XiaRepository.class)){
                        classCache.add(loadClass);
                    }
                }
            }
        }
        System.out.println(classCache);



    }
    @Override
    public Object doGetBean(String beanName){
        return beanDefinationsMap.get(beanName);
    }

    /**
     * 装配
     */
    public void doCreateBean()throws Exception{
        //判断
        if(classCache.size()==0){
            return;
        }
        //如果有别名 把别名作为key 存储 value就是当前这个类的实例
        for (Class<?> cl:classCache){
            Object instance=cl.newInstance();

            //默认别名首字母小写
            String alias=lowerClassName(cl.getSimpleName());
            //判断是否存在别名
            if(cl.isAnnotationPresent(XiaRepository.class)){
                XiaRepository xiaRepository=cl.getAnnotation(XiaRepository.class);
                //自定义别名
                if(!"".equals(xiaRepository.value())){
                     alias=xiaRepository.value();
                }
            }
            beanDefinationsMap.put(alias,instance);

            //判断当前是否实现了接口
           Class<?>[]interfaces= cl.getInterfaces();
            if(interfaces==null){
                continue;
            }
            //把当前接口的类路径作为key存储到容器
            for (Class<?>interf:interfaces){
                beanDefinationsMap.put(interf.getName(),instance);
            }


        }
    }


    public void pluplatebean()throws Exception{
        //判断
        if(classCache.size()==0){
            return;
        }
        for (Class<?>cl:classCache){
            //默认别名首字母小写
            String alias=lowerClassName(cl.getSimpleName());
            //判断是否存在别名
            if(cl.isAnnotationPresent(XiaRepository.class)){
                XiaRepository xiaRepository=cl.getAnnotation(XiaRepository.class);
                //自定义别名
                if(!"".equals(xiaRepository.value())){
                    alias=xiaRepository.value();
                }
            } else if(cl.isAnnotationPresent(XiaService.class)){
                XiaService xiaRepository=cl.getAnnotation(XiaService.class);
                //自定义别名
                if(!"".equals(xiaRepository.value())){
                    alias=xiaRepository.value();
                }
            }
           Object intsance=this.beanDefinationsMap.get(alias);
           Field []fields= cl.getDeclaredFields();
            for (Field field:fields){
                if(field.isAnnotationPresent(XiaAutowired.class)){
                    field.setAccessible(true);
                    XiaAutowired xiaAutowired=field.getAnnotation(XiaAutowired.class);
                    if(!"".equals(xiaAutowired.value())){
                        field.set(intsance,this.beanDefinationsMap.get(xiaAutowired.value()));
                    }else {
                        String fileName=field.getType().getName();
                        //按照类型装配
                        field.set(intsance,this.beanDefinationsMap.get(fileName));
                    }
                }
            }
        }
    }

    /**
     * 类名称首字母变为小写
     * @param className
     * @return
     */
    private String lowerClassName(String className){
        char[]ch=className.toCharArray();
        ch[0]+=32;//首字母小写
        return new String(ch);
    }
}
