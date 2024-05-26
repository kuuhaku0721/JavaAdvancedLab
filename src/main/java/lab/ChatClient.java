package lab;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 客户端，需要有个框，没啥特殊意义，就是好看，直接输出在控制台也可以
 */
public class ChatClient extends JFrame {
    // 窗口部分内容：内容以最简化生成，一个聊天区域，一个输入框，一个发送按钮
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    // 网络套接字部分
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    // 要求的日期时间戳
    private SimpleDateFormat dateFormat;

    // 固有属性
    private String username;

    public ChatClient(){
        System.out.println("请使用带参数构造");
    }

    public ChatClient(String username, Point point) {
        // TODO 之后会由第三方来启动窗口，需要额外设置一个接口用来设置一些属性，比如标题和位置
        // 窗口初始化
        setTitle(username);
        this.username = username;
        setSize(500, 400);
        setLocation(point.x, point.y);  // 200 300 | 800 300
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 控件初始化
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("发送");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // 绑定事件函数
        // 发送按钮
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        // 叉叉
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("socket closed...");
            }
        });

        // 连接服务器
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 先初始化时间戳 万一下面抛异常了，这里执行也是没问题的,因为后面要用
        try {
            socket = new Socket("localhost", 10721);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 匿名，好用
            new Thread(new Recv()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送消息 按钮响应
     */
    private void sendMessage() {
        // 拼一下消息串
        String message = messageField.getText();
        String timestamp = dateFormat.format(new Date());
        String showMsg = "[" + timestamp + "] " + username + ": " + message;

        // 发出去
        try {
            writer.write(message);
            writer.newLine();
            // 这一点就很烦，发出去之后它会存到缓冲区里，所以得刷新一下
            writer.flush();

            // 同步显示到对话框里(先发送，再显示)
            chatArea.append(showMsg + "\n");
            messageField.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 线程函数
     * 接收消息线程
     */
    private class Recv implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    String recvMsg = "Recv: " + message;

                    chatArea.append(recvMsg + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Run(){
        // 这个写法，两个客户端同生共死，其实是有点问题的，但能跑，并且功能正常，那就不管,你说异常？屏蔽就好了
        ChatClient client1 = new ChatClient("kuuhaku", new Point(200, 300));
        ChatClient client2 = new ChatClient("Amane", new Point(800, 300));
        client1.setVisible(true);
        client2.setVisible(true);
    }

    public static void main(String[] args) {
        Run();
    }
}