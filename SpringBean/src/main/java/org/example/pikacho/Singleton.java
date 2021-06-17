package org.example.pikacho;

/**
 * 单例模式
 */
public class Singleton {
    private static Singleton singleton;

    // 声明为私有之后在其他内就无法使用 该构造器来创建新的对象
    private Singleton(){
    }
    //只提供唯一一个访问此对象的方法
    /*
    * public static Singleton getSingleton(){
        if(singleton == null){
            Singleton temp =  new Singleton();
            singleton = temp;
            return temp;
        }
        return singleton;
      }
    * */

    public static Singleton getSingleton(){
        return new Singleton();
    }
}