package lab;


/**
 * (1) 编写一个网络聊天程序，要求服务端和客户端都能够显示聊天过程（类似QQ或微信，能够显示发送者和接收时间）。（基础） (clear)
 * (2) 服务端和客户端都能够显示历史消息，每条消息要有发送者和时间戳。 (不知道说的啥，如果指的是历史所有消息，大不了加个数据库)
 */

public class Lab06 {
    public void RunLab06(){
        // 单独开一个线程去执行服务器，然后本线程执行俩客户端
        // 但是，并不推荐这么跑，这么跑你会发现原本应该在两个控制台输出的东西会输出在一个控制台里面
        // 因为它们都是从Main里面出来的，都会输出到Main进程里面去
        // 推荐到ChatServer和ChatClient里面单独执行它们自己的Main函数（测试环境即如此）
        Thread server = new Thread(() -> {
            ChatServer.Run();
        });
        server.start();
        ChatClient.Run();
    }
}
