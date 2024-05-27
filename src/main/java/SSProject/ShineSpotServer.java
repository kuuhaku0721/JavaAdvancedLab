package SSProject;

/**
 * Origin: kuuhaku
 * Time: 0526
 * Stat: start
 * Desc: 课程要求的大作业程序设计，要求只说了要用六门课程中学习的技术，但是那样未免有点太意味がない
 *       既然是个大作业就要有个大作业的样子，最起码得是个完整的程序，比如有个UI界面之类的
 * From kuuhaku：
 *   This program may not earn you a high grade in the course
 *   because it is not "to the teacher's liking",
 *   but it will give you an understanding of Java desktop application programming,
 *   so familiarize yourself with this information before you begin your programming.
 */

import java.io.*;
import java.lang.reflect.*;
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
 * 主要任务包括，启动服务器，等待连接，转发消息等等，消息传递的格式自定义一个简单的吧，用的太复杂了怕老师看不懂
 */
public class ShineSpotServer {
    private List<ClientHandler> clients;
    private List<String> users;
    private MySQLHelper helper;

    public ShineSpotServer() throws Exception {
        clients = new ArrayList<>();
        users = new ArrayList<>();
        helper = new MySQLHelper();
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on [" + port + "] success...");

            // 输出一下客户端句柄类的信息，也就是，打日志
            getClassInfo(ClientHandler.class);

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
            sendMsg = "Group,";
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
     * 给所有用户发送消息，方便使用的
     * @param msg: 要发送的消息体
     */
    private void SendToAll(String msg) {
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

    /**
     * 反射机制的使用，获取客户端句柄类的信息并输出日志
     * @param cls：客户端句柄类
     */
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

    /**
     * 客户端句柄
     * 一个句柄就是一个单独的线程，负责处理该客户端所有的业务
     */
    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
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
                    switch (strs[0]) {
                        case "Self" -> {
                            users.add(strs[1]);

                            StringBuilder sendMsg = new StringBuilder("Online,");
                            for (String s : users) {
                                sendMsg.append(s).append(",");
                            }
                            SendToAll(sendMsg.toString());
                        }
                        case "Single" -> {
                            sender += strs[1];
                            targetName += strs[2];
                            msg += strs[3];
                        }
                        case "Group" -> {
                            sender += strs[1];
                            targetName = "nil";
                            msg += strs[3];
                        }
                        case "Change" -> {
                            String uname = strs[1];
                            String oldPwd = strs[2];
                            String newPwd = strs[3];
                            Map<String, String> userInfo = helper.readUserTable();
                            if (userInfo.containsKey(uname) && userInfo.get(uname).equals(oldPwd)) {
                                // 写入新内容
                                String sql = "update user set password = '" + newPwd + "' where uname = '" + uname + "';";
                                helper.executeSQL(sql);
                                sendTo("update success");
                            } else {
                                sendTo("update error");
                            }
                        }
                        default -> {
                            System.out.println("Server---接收到的消息有误，程序并没有按预期进行...");
                            System.out.println(message);
                        }
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

    public static void main(String[] args) throws Exception {
        ShineSpotServer server = new ShineSpotServer();
        server.start(10721);
    }
}
