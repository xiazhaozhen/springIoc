package org.xia.springframwork.webmvc.servlet;

import org.xia.springframwork.webmvc.annotation.GPAutowired;
import org.xia.springframwork.webmvc.annotation.GPController;
import org.xia.springframwork.webmvc.annotation.GPRequestMapping;
import org.xia.springframwork.webmvc.annotation.GPService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Created by sgl on 18/8/13.
 */
public class GPDispatcheServlet extends HttpServlet{

    private Properties contextConfig=new Properties();

    private List<String>classNameList=new ArrayList<>();

    private Map<String,Object> ioc=new HashMap<>();

    private Map<String,Method> handleMaping=new HashMap();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       //等待请求
        doDispatcher(req,resp);
    }
    private void doDispatcher(HttpServletRequest request,HttpServletResponse response)throws IOException{
        String url=request.getRequestURI();
        String contextPath=request.getContextPath();
        url=url.replace(contextPath,"").replaceAll("/+","/");
        if(!this.handleMaping.containsKey(url)){
            response.getWriter().write("NOT FOUND 404 !!");
            return;
        }
        Method method=this.handleMaping.get(url);
        System.out.println(method);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 读取配置文件
        doLoadConfig(config.getInitParameter("contextconfigLocation"));
        //读取配置文件 扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
        //实例化 刚刚扫描到的类
        doInstance();
        //自动化依赖注入
        doAutowired();
        //初始化  HandlerMapper
        initHandlerMapping();

    }
    private void doLoadConfig(String contextconfigLocation){
        InputStream is=this.getClass().getClassLoader().getResourceAsStream(contextconfigLocation);
        try {
            contextConfig.load(is);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }


    }
    private void doScanner(String scanner){
        URL url=this.getClass().getClassLoader().getResource("/"+scanner.replaceAll("\\.","/"));
        File classDir=new File(url.getFile());
        for (File file:classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(scanner+"."+file.getName());
            }else {
                String className=scanner+"."+file.getName().replace(".class","");
                classNameList.add(className);
            }
        }


    }
    private void doInstance(){
        if(classNameList.isEmpty()){
            return;
        }
        try {
            for(String className:classNameList){
                Class<?>clazz=Class.forName(className);
               // Object instance=clazz.newInstance();

                //
                if(clazz.isAnnotationPresent(GPController.class)){
                    Object instance=clazz.newInstance();
                    //spring beanId 默认是类名的首字母小写
                    String beanNmae=lowerFisterCase(clazz.getSimpleName());
                    ioc.put(beanNmae,instance);
                }else if(clazz.isAnnotationPresent(GPService.class)){
                    //1用自己的命名

                    //2默认的命名

                    //3把接口的实现类的引用赋值给接口

                    GPService gpService=clazz.getAnnotation(GPService.class);
                    String beanName=gpService.value();
                    if("".equals(beanName)){
                        beanName=lowerFisterCase(clazz.getSimpleName());
                    }
                    Object instance=clazz.newInstance();
                    ioc.put(beanName,instance);

                    Class<?> []interfances=clazz.getInterfaces();
                    for(Class<?>i:interfances){
                        ioc.put(i.getName(),instance);
                    }
                }else {
                    continue;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String lowerFisterCase(String simleName){
        char[] chars=simleName.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }
    private void doAutowired(){
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry:ioc.entrySet()){
            //赋值
            Field []fields=entry.getValue().getClass().getDeclaredFields();
            for (Field field:fields){
                if(!field.isAnnotationPresent(GPAutowired.class)){
                    continue;
                }
                GPAutowired autowired=field.getAnnotation(GPAutowired.class);
                String beanName=autowired.value().trim();
                if("".equals(beanName)){
                    beanName=field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),ioc.get(beanName));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }

    }
    private void initHandlerMapping(){
        if(ioc.isEmpty()){
            return;
        }
        for (Map.Entry<String,Object>entry:ioc.entrySet()){
            Class<?> clazz=entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(GPController.class)){
                String baseUrl="";
                if(clazz.isAnnotationPresent(GPRequestMapping.class)){
                    GPRequestMapping requestMapping=clazz.getAnnotation(GPRequestMapping.class);
                    baseUrl=requestMapping.value();
                }
                Method[]methods=clazz.getMethods();
                for (Method method:methods){
                    if(!method.isAnnotationPresent(GPRequestMapping.class)){
                        continue;
                    }
                    GPRequestMapping requestMapping=method.getAnnotation(GPRequestMapping.class);
                    String url=requestMapping.value();
                    url=(baseUrl+"/"+url).replaceAll("/+","/");
                    this.handleMaping.put(url,method);
                }
            }
        }
    }
}
