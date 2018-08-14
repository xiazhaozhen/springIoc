package org.xia.springframwork.ioc.xml;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * Created by sgl on 18/8/8.
 */
public class BeanXMLReaderUtil {

    private  static BeanXMLReaderUtil readerUtil=new BeanXMLReaderUtil();



    private BeanXMLReaderUtil(){

    }

    public static BeanXMLReaderUtil getInstance(){
        return  readerUtil;
    }







    /**
     * 通过配置文件 解析出框架中需要被扫描的包路径
     *
     * 容器的配置文件路径
     * @param configLocation
     * @return
     */
    public String handerXMLForScanPackage(String configLocation)throws Exception{
        InputStream inputStream=this.getClass().getClassLoader().getResourceAsStream(configLocation);
        //创建DON4J解析对象
        SAXReader reader=new SAXReader();
        //文档对象
        Document document=reader.read(inputStream);
        //当前的根元素
        Element rootElement=document.getRootElement();
        Element element=rootElement.element("compontent-scan");
        System.out.println(element.attributeValue("base-package"));
        return element.attributeValue("base-package");

    }

    public static void main(String []args)throws Exception{
        System.out.println(BeanXMLReaderUtil.getInstance().handerXMLForScanPackage("application.xml"));
    }
}
