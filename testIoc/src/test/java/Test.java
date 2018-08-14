import org.xia.springframwork.ioc.context.ApplicationContext;
import org.xia.springframwork.ioc.context.ClassPathXmlApplicationContext;

/**
 * Created by sgl on 18/8/14.
 */
public class Test {
    public static void main(String []args)throws Exception{
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("application.xml");
    }
}
