package lab;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("需要启动哪个实验直接调用相关类对象即可（初期设定，如果后面实验较为复杂则会更改启动方式）");
        // 一个对象对应一个实验
        Lab01 lab01 = new Lab01();
        Lab02 lab02 = new Lab02();
        Lab03 lab03 = new Lab03();


        lab01.RunLab01();
        // lab02.RunLab02();
        // lab03.RunLab03();
    }
}