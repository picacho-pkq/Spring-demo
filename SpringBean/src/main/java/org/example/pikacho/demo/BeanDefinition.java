package org.example.pikacho.demo;


//bean定义对象
public class BeanDefinition {
    // 类型
    private Class clazz;
    // bean的作用域
    private String scope;

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getScope() {
        return scope;
    }
}