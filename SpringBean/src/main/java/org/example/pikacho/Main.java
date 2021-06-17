package org.example.pikacho;

import org.example.pikacho.demo.AppConfig;
import org.example.pikacho.demo.MyApplicationContext;
import org.example.pikacho.demo.MyService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
       // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MySpringContext.class);
       // MyBean myBean = (MyBean) context.getBean("myBean");
       // myBean.test();

        //手写实现Spring容器
        MyApplicationContext myApplicationContext= new MyApplicationContext(AppConfig.class);
        MyService myService = (MyService) myApplicationContext.getBean("myService");
        System.out.println(myService.getOrderService());
        System.out.println(myService.getBeanName());

        // 测试单例bean和原型bean
       /* System.out.println(myApplicationContext.getBean("myService"));
        System.out.println(myApplicationContext.getBean("myService"));
        System.out.println(myApplicationContext.getBean("myService"));

        System.out.println("--------------以下是单例bean测试结果------------");

        System.out.println(myApplicationContext.getBean("myService1"));
        System.out.println(myApplicationContext.getBean("myService1"));
        System.out.println(myApplicationContext.getBean("myService1"));
        */


        // 验证单例 原型
        /*
           System.out.println(Singleton.getSingleton());
           System.out.println(Singleton.getSingleton());
           System.out.println(Singleton.getSingleton());
        */
    }
}
