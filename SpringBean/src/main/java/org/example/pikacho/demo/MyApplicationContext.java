package org.example.pikacho.demo;

import org.example.pikacho.BeanPostProcessor;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    public  Class clazz;
    // Class类型的成员变量

    //单例池
    private ConcurrentHashMap<String,Object> singletonMap =new ConcurrentHashMap<>();
    // BeanDefinition
    private ConcurrentHashMap<String,Object> beanDefinitionMap =new ConcurrentHashMap<>();

    //BeanPostProcessor
    private List<BeanPostProcessor> beanPostProcessorList=new ArrayList<>();

    // 构造方法
    public MyApplicationContext(Class clazz) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.clazz=clazz;
        // 扫描路径
        scan(clazz);

        // 单例bean处理
        for (Map.Entry<String, Object> entry : beanDefinitionMap.entrySet()) {
            String key = entry.getKey();
            BeanDefinition beanDefinition = (BeanDefinition)entry.getValue();
            if(beanDefinition.getScope().equals("singleton")){
                Object o= createBean("myService",beanDefinition);
                singletonMap.put(key,o);
            }
        }

    }
    private void scan(Class clazz) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //扫描包的逻辑代码
        //1.获取传入的配置类上的@ComponentScan里面的参数,包的扫描路径
        ComponentScan componentScan = (ComponentScan)clazz.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScan.value();
        System.out.println(path);

        //2.获取到包扫描路径后，需要根据该路径获取到该路径下所有的文件
        // 获取类加载器
        ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
        // 获取path下所有资源
        URL resource = classLoader.getResource(path.replace(".", "/"));
        // 获取文件
        File file = new File(resource.getFile());
        if(file.isDirectory()){
            // 如果是文件夹
            File[] files = file.listFiles();
            for (File f: files) {
                if(f.getAbsolutePath().endsWith(".class")){
                    String absolutePath = f.getAbsolutePath();
                    String filePath = absolutePath.substring(absolutePath.indexOf("org"), absolutePath.indexOf(".class"));
                    String className = filePath.replace("\\", ".");
                    // 通过类加载器，加载类
                    Class<?> aClazz = classLoader.loadClass(className);

                  if(BeanPostProcessor.class.isAssignableFrom(aClazz)){
                        BeanPostProcessor instance = (BeanPostProcessor)aClazz.getDeclaredConstructor().newInstance();
                        beanPostProcessorList.add(instance);
                  }


                    if(aClazz.isAnnotationPresent(Component.class)){
                        // 如果该类上存在@Component注解
                        Component component =aClazz.getDeclaredAnnotation(Component.class);
                        // 定义一个beanDefinition对象
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setClazz(aClazz);

                        if(aClazz.isAnnotationPresent(Scope.class) ){
                            //该类上有@Scope注解注释
                            beanDefinition.setScope(aClazz.getDeclaredAnnotation(Scope.class).value());
                        }else{
                            //默认单例bean
                            beanDefinition.setScope("singleton");
                        }
                        String beanName = component.value();
                        beanDefinitionMap.put(beanName,beanDefinition);
                    }
                }
            }
        }


    }

    // getBean方法,根据名称获取一个bean对象
    public Object getBean(String name){
        // 根据bean名称，获取bean定义
        if(beanDefinitionMap.containsKey(name)){
            BeanDefinition beanDefinition =(BeanDefinition) beanDefinitionMap.get(name);
            if(beanDefinition.getScope().equals("singleton")){
                // 从单例池中获取
                return  singletonMap.get(name);
            }else {
                // 创建bean
                return createBean("myService", beanDefinition);
            }
        }else{
            // 不存在对应的bean
            throw new NullPointerException();
        }
    }

    private Object createBean(String beanName,BeanDefinition beanDefinition) {
        try {
            // 根据beanDefinition对象创建bean对象
            Class clazz = beanDefinition.getClazz();
            Object instance = clazz.newInstance();
            //获取类的所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            for (Field field: fields) {
                // 如果该成员变量被@Autowired注解标识
                if(field.isAnnotationPresent(Autowired.class)){
                    // 成员变量名称
                    String name = field.getName();
                    // 根据名称获取bean
                    Object bean = getBean(name);
                    field.setAccessible(true);
                    // 将获取到的bean设置到类对象中
                    field.set(instance,bean);
                }
            }
            if(instance instanceof BeanNameAware){
                // 如果instance实现类BeanNameAware接口
                ((BeanNameAware) instance).setBeanName(beanName);
            }
            // 初始化之前
           beanPostProcessorList.forEach(item->{
                item.postProcessBeforeInitialization(instance, beanName);
            });


            // 初始化
            if(instance instanceof InitializingBean){
                //如果instance实现类InitializingBean接口,则调用其抽象方法
                ((InitializingBean) instance).afterPropertiesSet();
            }
            // 初始化之后
           beanPostProcessorList.forEach(item->{
                item.postProcessAfterInitialization(instance, beanName);
            });

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
