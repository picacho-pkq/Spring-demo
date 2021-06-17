package org.example.pikacho;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"org.example.pikacho"})
public class MySpringContext {
    public MySpringContext(){
        System.out.println("容器初始化中。。。。");
    }
}
