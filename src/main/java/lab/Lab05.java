package lab;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * (1) 编写一个多线程协作的程序，解决“生产者和消费者”问题 或者 “读者与写者” 问题 或者 “哲学家进餐”问题。（基础）   (clear)
 *     卷成这个13样子，这个所谓的 "或者" 等于 &&
 */
public class Lab05 {
    private Timer timer = new Timer();  // 一个用来让线程结束掉的计时器
    public void producerConsumerExample() {
        LinkedList<Integer> buffer = new LinkedList<>();
        int maxSize = 10;
        Thread producer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (buffer) {
                    while (buffer.size() == maxSize) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // 设置中断标志位
                        }
                    }
                    if (!Thread.currentThread().isInterrupted()) { // 检查中断标志位
                        int value = (int) (Math.random() * 100);
                        buffer.add(value);
                        System.out.println("生产: " + value);
                        buffer.notifyAll();
                    }
                }
            }
        });
        Thread consumer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (buffer) {
                    while (buffer.size() == 0) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // 设置中断标志位
                        }
                    }
                    if (!Thread.currentThread().isInterrupted()) { // 检查中断标志位
                        int value = buffer.removeFirst();
                        System.out.println("消费: " + value);
                        buffer.notifyAll();
                    }
                }
            }
        });
        // 标志位大法 好！

        producer.start();
        consumer.start();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                producer.interrupt();
                consumer.interrupt();
                timer.cancel();
            }
        }, 100);    // 100毫秒，实测证明，就算只有1s，数据量也很大
    }
    public void readerWriterExample() {
        // 解决 == 上锁
        ReadWriteLock rwl = new ReentrantReadWriteLock();
        Map<String, String> data = new HashMap<>();
        /**
         * 这么执行是有问题的，线程可以同步，但是当前这个函数可是按顺序执行的
         * 按照目前的写法，它一定是先启动写者，然后启动读者
         * 然后线程还跑的贼快，写者一启动就会刷刷刷写进去一堆数据
         * 在一段时间之后才会看到有读者读数据，再加上还有定时器决定线程的存亡
         * 这就导致直到程序结束前才看到了几个读者
         */
        Thread writer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rwl.writeLock().lock();
                try {
                    data.put("key", "value");
                    System.out.println("写者 写入数据");
                } finally {
                    // 释放锁
                    rwl.writeLock().unlock();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread reader = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rwl.readLock().lock();
                try {
                    String value = data.get("key");
                    System.out.println("读者 读取数据");
                }  finally {
                    rwl.readLock().unlock();
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        writer.start();
        reader.start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                writer.interrupt();
                reader.interrupt();
                timer.cancel(); // 取消定时器
                System.exit(0);
            }
        }, 1000);
    }

    public void diningPhilosophersExample() {
        // 五个哲学家，五根筷子
        int numPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        Object[] chopsticks = new Object[numPhilosophers];
        // 初始化
        for (int i = 0; i < numPhilosophers; i++) {
            chopsticks[i] = new Object();
        }
        for (int i = 0; i < numPhilosophers; i++) {
            Object leftChopstick = chopsticks[i];
            Object rightChopstick = chopsticks[(i + 1) % numPhilosophers];
            // 就是每个人只能拿到自己左右手边的筷子
            if (i == numPhilosophers - 1) {
                philosophers[i] = new Philosopher(rightChopstick, leftChopstick);
            } else {
                philosophers[i] = new Philosopher(leftChopstick, rightChopstick);
            }
            Thread t = new Thread(philosophers[i]);
            t.start();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    t.interrupt();
                    // t.stop();    // 没有很好的起效果，不知道为啥
                    timer.cancel(); // 取消定时器
                    System.exit(0);  // 不如直接挂掉来的快
                }
            }, 1000);
        }
    }

    private static class Philosopher implements Runnable {
        private Object leftChopstick;
        private Object rightChopstick;

        public Philosopher(Object leftChopstick, Object rightChopstick) {
            this.leftChopstick = leftChopstick;
            this.rightChopstick = rightChopstick;
        }

        // 就把这个类当作一个继承自线程类的类，它可以自己去跑，只要有个线程函数就行
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                doAction("我在思考人生的哲学....");
                synchronized (leftChopstick) {
                    doAction("拿起了左手边的筷子");
                    synchronized (rightChopstick) {
                        doAction("拿起了右手边的筷子  然后立马开始干饭");
                        doAction("放下了手中的干饭神器(右)");
                    }
                    doAction("放下了手中的干饭神器(左)");
                }
            }
        }
        private void doAction(String msg) {
            System.out.println(Thread.currentThread().getName() + ": " + msg);
            try {
                // 执行完之后休眠一会，不要跑的太快
                Thread.sleep((int) (Math.random() * 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void RunLab05() {
        System.out.print("涉及到线程的问题，我并不推荐你直接启动，推荐以单个函数的形式的方式启动：\n" +
                "producerConsumerExample----生产者和消费者问题\n" +
                "readerWriterExample----读者与写者问题\n" +
                "diningPhilosophersExample----哲学家进餐问\n" +
                "如果你执意要这么启动的话，请输入y以确认:");
        Scanner sc = new Scanner(System.in);
        String cmd = sc.next();
        if(cmd.equals("y")){
            System.out.println("----生产者和消费者问题----");
            producerConsumerExample();

            System.out.println("----读者与写者问题----");
            readerWriterExample();

            System.out.println("----哲学家进餐问题----");
            diningPhilosophersExample();
        }else {
            System.out.println("感谢你的配合");
            return;
        }
    }
}

class runner{
    public static void main(String[] args) {
        Lab05 lab05 = new Lab05();
        // lab05.producerConsumerExample();
        // lab05.readerWriterExample();
        lab05.diningPhilosophersExample();
    }
}

