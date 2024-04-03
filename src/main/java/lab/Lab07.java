package lab;


import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * (1) ①编写一个基类，至少包含一种方法； (clear)
 *     ②自定义一个注解，包含两个属性；   (clear)
 *     ③将注解加入到基类；              (clear)
 *     ④编写子类，继承该基类，          (clear)
 *     ⑤重写基类的一个方法，            (clear)
 *     ⑥新增一个方法，                 (clear)
 *     ⑦继承基类的注解、                (clear)
 *     ⑧实现三种内建注解（@Override、@Deprecated、@SuppressWarnings），其中@SuppressWarnings至少实现三种警告的压制。（基础） (clear)
 * (2) 自定义一个带属性的注解，并进行简单测试。 (clear)
 */

// 2、自定义一个注解
@Retention(RetentionPolicy.RUNTIME)     // 元注解，运行时可以通过反射机制获取到这个注解，从而实现对注解进行解析和处理
@Target(ElementType.TYPE)               // 元注解，指定注解可以应用的目标元素类型
@Inherited  // 7、可以让派生类继承基类的注解
@interface KuuhakuAnnotation {
    // 2、包含两个属性
    String name() default "kuuhaku";
    int age() default 10721;            // 第(2)部分 自定义带属性的注解
}

// 第(2)部分  并进行简单测试
@KuuhakuAnnotation
class TestCustomAnnotation{
    public void testAnnotation(){
        System.out.println("对自定义带属性的注解进行简单测试：获取注解里面的两个属性");
        Class<?> annotatedClass = TestCustomAnnotation.class;
        if (annotatedClass.isAnnotationPresent(KuuhakuAnnotation.class)) {
            KuuhakuAnnotation annotation = annotatedClass.getAnnotation(KuuhakuAnnotation.class);
            String name = annotation.name();
            int age = annotation.age();
            System.out.println("Name: " + name);
            System.out.println("Age: " + age);
        }
    }
}

// 1、编写一个基类
@KuuhakuAnnotation(age = 721)       // 3、将注解加入到基类
class BaseCls {
    @Deprecated                                                         // 8、实现三种内建注解之二 @Deprecated 过时  代码提示器里画横线的那些，指明这些方法过时了不推荐使用
    public void print(){
        // 1、至少
        System.out.println("包含一种方法");
        // 获取注解的属性，然后打印输出
        System.out.println("输出注解中的两个属性: ");
        KuuhakuAnnotation annotation = BaseCls.class.getAnnotation(KuuhakuAnnotation.class);
        String name = annotation.name();
        int age = annotation.age();
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
    }

    public void beOverride(){
        System.out.println("这个是给Override用的，上面那个给注解用了");
    }
}

// 4、编写子类，继承该基类
class DerivedCls extends BaseCls {
    // 5、重写基类的一个方法
    @Override                                                           // 8、实现三种内建注解之一 @Override 重写 很常见
    public void beOverride(){
        System.out.println("这个是给Override用的，上面那个给注解用了");
    }

    // 6、新增一个方法
    @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})         // 8、实现三种内建注解之三 @SuppressWarnings 用于抑制特定类型的警告
    public void suppress_warn() {
        System.out.println("实现三种警告的压制\n请到编译器输出中查看警告信息");
        testUncheckedWarning();
        testRawtypesWarning();
        testDeprecationWarning();
    }
    /**
     * 下面是给SuppressWarnings注解测试使用的，分别触发三个异常警告信息
     * 如果需要看到这几个警告，需要在编译器的输出里面查看，程序运行起来是不会报错的
     * 编译方法：当前页面(或者右面Maven)--随便找个空白处右键--Run Maven--往上面找，找到compile--然后看下面的输出，会看到几个[WARNING]
     */
    public void testUncheckedWarning() {
        List list = new ArrayList();
        list.add("element");
    }
    public void testRawtypesWarning() {
        List list = new ArrayList();
        list.add("element");
    }
    private void testDeprecationWarning() {
        Date date = new Date(2020, 1, 1);
    }


    /**
     * 下面这个方法是给实验八用的
     */
    public void SendToLab08(){
        System.out.println("虽然你没啥用，但姑且打印一句话看看");
    }
}



public class Lab07 {
    public void RunLab07(){
        System.out.println("第(1)部分");
        DerivedCls deriv = new DerivedCls();
        deriv.suppress_warn();
        System.out.println("当前调用的是派生类，方法是基类的print方法，因此存在一个继承关系：任务(1)⑦");
        deriv.print();

        System.out.println("第(2)部分");
        TestCustomAnnotation testCustomAnnotation = new TestCustomAnnotation();
        testCustomAnnotation.testAnnotation();
    }

}



