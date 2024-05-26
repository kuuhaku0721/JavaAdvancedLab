package lab;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer {
    private List<ClientHandler> clients;
    private SimpleDateFormat dateFormat;

    public ChatServer() {
        clients = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on [" + port + "] success...");

            while (true) {
                // 服务器等待客户端连接，连上来一个开一个线程去处理
                // 这个架构是不是很眼熟...
                Socket socket = serverSocket.accept();
                System.out.println("Client [" + socket + "] connected success....");

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
    private void Send(String message) {
        String timestamp = dateFormat.format(new Date());
        String msg = "[" + timestamp + "] " + message;

        for (ClientHandler client : clients) {
            client.sendTo(msg);
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

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;

        /**
         * socket理解为客户端本体就行
         * socket在，客户端在
         * socket亡，客户端亡(对服务器来说，观测不到就是不存在)
         */
        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                // 匿名的形式是真好用啊...
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 线程主函数
         * 负责接收消息，然后将其发送给其他客户端，实现聊天
         * 就是以服务器为中转，比如 A->B：A发给服务器--服务器收到--服务器发给B
         */
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Server received: " + message);
                    // 发送出去
                    Send(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // finally是什么意思就不用解释了
                // 这种形式应该也是很熟悉的，http短链接，用完就断
                // 也可以用长连接，但是用短链接的话，可以少写个函数
                CloseSock(this);
            }
        }

        /**
         * 解释一下这个函数的作用：
         * 当前这个类在服务器里面，也就是属于服务器跳台，它的所有行为都属于服务器行为
         * 也就是说，下面的这个sendto函数就是服务器将要发往的意思
         * 要发送肯定需要一个socket(就是这里的流),而这个socket保存在一个个的句柄里面(就是这个handler)
         * 所以需要有一个接口来向客户端写回去消息
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

    public static void Run(){
        ChatServer server = new ChatServer();
        server.start(10721);
    }

    public static void main(String[] args) {
        Run();
    }
}