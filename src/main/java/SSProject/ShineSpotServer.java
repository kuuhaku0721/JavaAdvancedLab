package SSProject;

/**
 * Origin: kuuhaku
 * Time: 0526
 * Stat: start
 * Desc: 课程要求的大作业程序设计，要求只说了要用六门课程中学习的技术，但是那样未免有点太意味がない
 *       既然是个大作业就要有个大作业的样子，最起码得是个完整的程序，比如有个UI界面之类的
 * 后继者请注意：
 *   This program may not earn you a high grade in the course
 *   because it is not "to the teacher's liking",
 *   but it will give you an understanding of Java desktop application programming,
 *   so familiarize yourself with this information before you begin your programming.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务器主类
 * 负责完成服务器的工作
 * 使用《实验：网络API》等等
 * 主要任务包括，启动服务器，等待连接，转发消息等等，消息传递的格式自定义一个简单的吧，用的太复杂了怕老师看不懂
 */
public class ShineSpotServer {
    private List<ClientHandler> clients;
    public ShineSpotServer() {
        clients = new ArrayList<>();
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on [" + port + "] success...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client [ " + socket + " ] connected success....");

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 向所有已连接的客户端转发消息
     */
    private void Send(String sender, String targetName, String message) {
        String sendMsg;
        if(targetName.equals("nil")) {
            // 群聊
            sendMsg = "Group";
        } else {
            // 私聊
            sendMsg = "Single,";
        }
        sendMsg += sender + "," + targetName + "," + message;
        for (ClientHandler client : clients) {
            client.sendTo(sendMsg);
        }
    }
    /**
     * 客户端断开连接，清理资源
     */
    private void CloseSock(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client [" + client.socket + "] disconnected...");

        // 关闭套接字
        try {
            client.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 客户端句柄
     * 一个句柄就是一个单独的线程，负责处理该客户端所有的业务
     */
    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private String userName;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 线程主函数
         * 负责接收消息，然后将其发送给其他客户端，实现聊天
         */
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Server received: " + message);
                    String[] strs = message.split(",");
                    String sender = "";
                    String targetName = "";
                    String msg = "";
                    if(strs[0].equals("Self")) {
                        this.userName = strs[2];
                        // 把当前在线的成员信息发出去
                        StringBuilder sendMsg = new StringBuilder("Online,");
                        for (ClientHandler client : clients) {
                            sendMsg.append(client.userName).append(",");
                        }
                        sendTo(sendMsg.toString());
                    } else if(strs[0].equals("Single")){
                        sender += strs[1];
                        targetName += strs[2];
                        msg += strs[3];
                    } else if(strs[0].equals("Group")) {
                        sender += strs[1];
                        targetName = "";
                        msg += strs[3];
                    } else if(strs[0].equals("Change")) {
                        // TODO 待办 修改密码的逻辑
                    } else {
                        System.out.println("Server---接收到的消息有误，程序并没有按预期进行...");
                        System.out.println(message);
                    }
                    Send(sender, targetName, msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 服务器发送给对应的客户端消息
         * @param message: 要发送的消息体
         */
        public void sendTo(String message) {
            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ShineSpotServer server = new ShineSpotServer();
        server.start(10721);
    }
}
