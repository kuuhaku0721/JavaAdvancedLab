package lab;

import java.util.Scanner;

class PrintThread extends Thread{
    private static Object lock = new Object();
    private String strA;
    private int number = 1;
    private String strSharp;
    public PrintThread(String s1, int count, String s2) {
        this.strA = s1;
        this.number = count;
        this.strSharp = s2;
    }

    public void run(){
        synchronized (lock){
            for (int i = 0; i < number; i++){
                System.out.print(strA + "" + (i + 1) + "" + strSharp);
                lock.notifyAll();
            }
        }
    }
}

public class newOne {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        String str = sc.next();
        String[] strs = str.split(",");
        int cnt = Integer.valueOf(strs[1]);

        int count = 26;
        int threadCount = 3;

        PrintThread t = new PrintThread(strs[0], cnt, strs[2]);
        t.start();
        t.join();


    }
}
