package lab;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 1.	Java异常处理机制
 * 目标：掌握Java异常处理机制和常见的Java异常处理方法。
 * (1) 常见异常的捕获与处理与try…catch…finally的应用（基础）。 (clear)
 * (2) 异常的抛出。   (clear)
 * (3) 自定义异常。   (clear)
 */

class KuuhakuException extends Exception {
    public KuuhakuException(String message) {
        super(message);
    }
}

public class Lab01 {
    private void Exception_NullPointerException() {
        try {
            String str = null;
            System.out.println(str.length());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void Exception_ClassNotFoundException(){
        try {
            Class.forName("lab.OneNotExistClass");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void Throw_NullPointerException() throws NullPointerException {
        String str = null;
        System.out.println(str.length());
    }
    private void Throw_ClassNotFoundException() throws ClassNotFoundException{
        Class<?> cls = Class.forName("lab.OneNotExistClass");
        cls.getClasses();
    }

    private void Exception_ClassCastException(){
        try {
            Object obj = "Hello";
            Integer num = (Integer) obj;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
    private void Exception_IllegalArgumentException(){
        try {
            int age = -1;
            if (age < 0) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    private void Exception_InputMismatchException(){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("随便输入一个不是数字的东西: ");
            int number = scanner.nextInt();
            System.out.println("正确输出：" + number);
        } catch (InputMismatchException e) {
            e.printStackTrace();
        }
    }
    private void Exception_IllegalAccessException(){
        try {
            Field field = String.class.getDeclaredField("value");
            field.setAccessible(false);
            Object value = field.get("Hello"); // 触发非法访问异常
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    private void Exception_SQLException(){
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db",
                    "username",
                    "password");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM non_existing_table");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void Exception_IOException(){
        try {
            File file = new File("one_not_exist_file.txt");
            FileReader fr = new FileReader(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ThrowException() throws Exception{
        System.out.println("异常的抛出就是自己不处理，交给上级去处理，" +
                "这里就从刚才的异常里挑几个出来使用throw的形式触发");
        // 启动的时候挑一个启动，每次抛出只能处理一个，两个都启动你会发现结果只出现了一个
        Throw_NullPointerException();
        Throw_ClassNotFoundException();
    }

    public void CustomException(){
        System.out.println("自定义异常的try...catch和throw");
        try {
            throw new KuuhakuException("这个是自定义的Kuuhaku异常");
        } catch (KuuhakuException e) {
            e.printStackTrace();
        }
    }

    /**
     * 三部分分开启动
     */
    public void RunException(){
        Exception_NullPointerException();
        Exception_ClassNotFoundException();
        Exception_ClassCastException();
        Exception_IllegalArgumentException();
        Exception_InputMismatchException();
        Exception_IllegalAccessException();
        Exception_SQLException();
        Exception_IOException();
    }
    /**
     * 一次启动三个
     * @throws Exception
     */
    public void RunLab01() throws Exception {
        RunException();
        // 自定义异常需要在抛出之前，不然一个异常抛出之后就直接一路向上了，看不到结果
        CustomException();
        ThrowException();

    }

}



