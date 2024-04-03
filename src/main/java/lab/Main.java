package lab;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Class<?> cls;
        String labName = "lab.Lab0";
        while(true)
        {
            System.out.println("----输入想要启动的实验号: ----");  // 部分实验内部的原因，可能运行一次就挂了，所以可能需要手动重复开启
            int cmd = sc.nextInt();
            if(cmd > 10 || cmd < 1)
                break;
            cls = Class.forName(labName + cmd);
            switch(cmd)
            {
                case 1:
                    ((Lab01) cls.newInstance()).RunLab01();
                    break;
                case 2:
                    ((Lab02) cls.newInstance()).RunLab02();
                    break;
                case 3:
                    ((Lab03) cls.newInstance()).RunLab03();
                    break;
                case 4:
                    ((Lab04) cls.newInstance()).RunLab04();
                    break;
                case 5:
                    System.out.println("不建议这样启动，推荐去Lab05类中单独启动，如果你执意要在这启动，请输入y以确定：");
                    String c = sc.next();
                    if("y".equals(c))
                        ((Lab05) cls.newInstance()).RunLab05();
                    else
                        System.out.println("感谢你的配合");
                    break;
                case 6:
                    ((Lab06) cls.newInstance()).RunLab06();
                    break;
                case 7:
                    ((Lab07) cls.newInstance()).RunLab07();
                    break;
                case 8:
                    ((Lab08) cls.newInstance()).RunLab08();
                    break;
                case 9:
                    // ((Lab09) cls.newInstance()).RunLab09();
                    break;
                case 10:
                    // ((Lab010) cls.newInstance()).RunLab010();
                    break;
            }
        }
        System.out.println("程序结束");
    }
}