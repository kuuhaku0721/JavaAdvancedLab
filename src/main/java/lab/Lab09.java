package lab;


/**
 * (1) 按照四大函数式接口的模式自定义四个接口 (clear)
 *     编写一个类 (clear)
 *     分别用“标准Lambda表达式”和“方法引用”两种方式调用这四个接口 (clear)
 *     并且输出调用结果。（基础） (clear)
 */
/**
 * 四大函数式接口：
 * Supplier（供应商）：该接口不接受任何参数，返回一个结果。
 * Consumer（消费者）：该接口接受一个参数，但不返回结果。
 * Function（函数）：该接口接受一个参数，并返回一个结果。
 * Predicate（断言）：该接口接受一个参数，并返回一个布尔值结果。
 */
// 自定义四个函数式接口
interface Func_Supplier {
    String supply();
}
interface Func_Consumer {
    void sayHelloWorld(String msg);
}
interface Func_Function {
    String concat(String s1, String s2);
}
interface Func_Predicate {
    boolean asser(int value);
}

public class Lab09 {
    public void RunLab09(){
        System.out.println("-----------用“标准Lambda表达式”调用四个接口-----------");
        Func_Supplier supplier = () -> "hello world";
        Func_Consumer consum = msg -> System.out.println("hello world, " + msg);
        Func_Function function = (s1, s2) -> s1 + "][" + s2;
        Func_Predicate predica = value -> value > 0;

        // 并且输出调用结果
        System.out.println("Supplier: " + supplier.supply());
        System.out.print("Consumer: "); consum.sayHelloWorld("kuuhaku");
        System.out.println("Function: " + function.concat("hello", "world"));
        System.out.println("Predicate: " + predica.asser(721));

        System.out.println("-----------用”方法引用“调用四个接口-----------");
        Func_Supplier func_supplier = Lab09::supplyHelloWorld;
        Func_Consumer func_consum = Lab09::printMessage;
        Func_Function func_function = Lab09::concatStrings;
        Func_Predicate func_predica = Lab09::checkPositive;

        // 并且输出调用结果
        System.out.println("Supplier: " + func_supplier.supply());
        System.out.print("Consumer: "); func_consum.sayHelloWorld("kuuhaku");
        System.out.println("Function: " + func_function.concat("hello", "world"));
        System.out.println("Predicate: " + func_predica.asser(-721));
    }
//    public static void main(String[] args) {
//        RunLab09();
//    }

    private static String supplyHelloWorld() {
        return "hello world";
    }

    private static void printMessage(String msg) {
        System.out.println("hello world, " + msg);
    }

    private static String concatStrings(String s1, String s2) {
        return s1 + "][" + s2;
    }

    private static boolean checkPositive(int value) {
        return value > 0;
    }
}










