package lab;


import java.lang.reflect.*;

/**
 * (1) 创建一个父类和一个继承自该父类的子类，要求包含四种修饰符的属性、四种修饰符的方法和四种修饰符的构造函数，部分函数要定义抛出异常。
 *     使用反射机制获取并显示这些属性、方法和构造函数的名称、所在的类、类型、参数类型、异常和返回类型。（基础）  (clear)
 * (2) 使用newInstance()和newInstance(Object[] args)两种方法实例化对象，包括调用有参构造函数。  (clear)
 */

class Base {
    public String name;
    protected int age;
    private double bonus;
    boolean flag;
    // 四种修饰符的方法
    public void publicMethod() {
        System.out.println("public method.");
    }
    protected void protectedMethod() {
        System.out.println("protected method.");
    }
    private void privateMethod() {
        System.out.println("private method.");
    }
    void defaultMethod() {
        System.out.println("default method.");
    }
    // 四种修饰符的构造函数  默认无参构造抛出异常
    public Base() {
        System.out.println("public constructor: Base");
    }
    public Base(String name) {
        System.out.println("public constructor: Base" + ", para: " + name);
    }
    protected Base(int age) {
        System.out.println("protected constructor");
    }
    private Base(double bonus) {
        System.out.println("private constructor");
    }
    Base(boolean flag) throws KuuhakuException {
        throw new KuuhakuException("这个是自定义的Kuuhaku异常");
    }
}

/**
 *     public String name;
 *     protected int age;
 *     private double bonus;
 *     boolean flag;
 */
class Derived extends Base {
    // 子类
    public Derived() {
        super();
        System.out.println("public constructor: Derived");
    }
    // 有参不同作用域
    public Derived(String name) {
        super();
        System.out.println("public constructor: Derived" + ", para: " + name);
    }
    protected Derived(int age) {
        super(age);
    }
//    // 很显然，没法调用父类的私有方法，异常我能忍，报错我忍不了
//    private Derived(double bonus) {
//        super(bonus);
//    }

    Derived(boolean flag)  throws Exception  {
        super(flag);
    }
    public void print(){
        System.out.println("啊啦啦~~我被创建啦");
    }
}

// 使用反射机制获取并显示这些属性、方法和构造函数的名称、所在的类、类型、参数类型、异常和返回类型
/**
 * 属性
 * 方法：包含 名称、所在类、类型、参数 异常
 * 返回顺序所在类提到最前面，剩下的按照声明顺序返回
 */
public class Lab04 {
    private void getClassInfo(Class<?> cls) {
        // 1、属性
        Field[] fields = cls.getDeclaredFields();
        System.out.println("属性:");
        for (Field field : fields) {
            System.out.print(field.getDeclaringClass().getName() + "--");      // 所在类
            System.out.print(Modifier.toString(field.getModifiers()) + "--");  // 修饰符
            System.out.print(field.getType().getName() + "--");                // 类型
            System.out.print(field.getName());                                 // 名称
            System.out.println();
        }


        // 2、方法
        Method[] methods = cls.getDeclaredMethods();
        System.out.println("方法:");
        for (Method method : methods) {
            System.out.print(method.getDeclaringClass().getName() + "--");     // 所在类
            System.out.print(Modifier.toString(method.getModifiers()) + "--"); // 修饰符
            System.out.print(method.getReturnType().getName() + "--");         // 返回值类型
            System.out.print(method.getName());                                // 名称
            // 参数列表
            Parameter[] parameters = method.getParameters();
            if (parameters.length > 0) {
                System.out.print(" (");
                for (Parameter parameter : parameters) {
                    System.out.print(parameter.getType().getName() + " " + parameter.getName() + ", "); // 参数列表：类型 名称
                }
                System.out.print(") ");
            }
            // 异常
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            if (exceptionTypes.length > 0) {
                System.out.print("throws ");
                for (Class<?> exceptionType : exceptionTypes) {
                    System.out.print(exceptionType.getName());
                }
            }
            System.out.println();
        }

        // 3、构造函数
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        System.out.println("构造函数:");
        for (Constructor<?> constructor : constructors) {
            System.out.print(constructor.getDeclaringClass().getName() + "--");         // 类名
            System.out.print(Modifier.toString(constructor.getModifiers()) + "--");     // 修饰符
            System.out.print(constructor.getName());                                    // 名称

            // 构造函数也可能有参
            Parameter[] parameters = constructor.getParameters();
            if (parameters.length > 0) {
                System.out.print(" (");
                for (Parameter parameter : parameters) {
                    System.out.print(parameter.getType().getName() + " " + parameter.getName() + ", ");
                }
                System.out.print(")");
            }
            System.out.println();
        }
        System.out.println("======显示完毕======");
    }

    private void ShowClsInfo(){
        System.out.println("----父类----");
        getClassInfo(BaseCls.class);
        System.out.println("----子类----");
        getClassInfo(Derived.class);
    }

    /**
     * 使用newInstance()和newInstance(Object[] args)两种方法实例化对象，包括调用有参构造函数
     * 无参构造一个 有参构造一个
     * 有参的传的int
     */
    // 这里如果通过提示抛异常的话，它自动添加的异常长这样：throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
    // 略微有那~~~么一点长，干脆一个Exception解决，测试环境可以如此，实际开发不推荐这样
    // 推荐使用try catch进行捕获，然后再抛：捕获能够快速定位错误地点，方便日志调试，throw交给上级去处理
    private void InstanceExample() throws Exception {
        // 类
        Class<?> cls = Derived.class;

        System.out.println("----使用 newInstance() 实例化对象[无参]-----");
        Derived d1 = (Derived) cls.newInstance();
        d1.print();

        System.out.println("\n----使用 newInstance(Object[] args) 实例化对象[有参]-----");
        Object[] args = {"kuuhaku"};  // 准备参数
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        Constructor<?> constructor = cls.getConstructor(parameterTypes);
        Derived d2 = (Derived) constructor.newInstance(args);
        d2.print();
    }

    public void RunLab04() throws Exception {
        ShowClsInfo();

        InstanceExample();
    }
}



