package SSProject;


/**
 * 客户端类
 * 负责创建UI界面，实现连接服务端的功能，发送消息，显示聊天记录等等
 * 发送给服务端的消息依旧使用自定义格式，不同的类型以UI界面来区分，群聊或者私聊
 */


/**
 * 任务计划：
 * 第一步：完成UI界面的搭建
 * 2、UI界面的功能测试
 * 3、初步完成网络连接部分
 * 4、完成服务器部分的接口搭建
 * 5、测试客户端和服务器的连通性
 * 6、完善其他功能的开发
 */


import lab.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ShineSpotClient extends JFrame {
    private JPanel leftPanel;
    private JList<String> userList;
    private JButton privateChatButton;
    private JButton groupChatButton;
    private JButton changePasswordButton;

    private JPanel rightPanel;
    private CardLayout cardLayout;
    private JPanel privateChatPanel;
    private JPanel groupChatPanel;
    private JPanel changePasswordPanel;
    private JTextArea privateChatTextArea;
    private JTextArea groupChatTextArea;
    private JScrollPane userListScrollPane;

    private int client_id;
    private String client_name;
    private MySQLHelper helper;
    private List recentMessage;

    // 网络套接字部分
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    // 要求的日期时间戳
    private SimpleDateFormat dateFormat;

    public ShineSpotClient(String name) {
        // 初始化客户端信息
        Random random = new Random();
        client_id = random.nextInt(255) + 1;
        client_name = name;

        // 连接服务器，并把自己信息发送给服务器
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 先初始化时间戳 万一下面抛异常了，这里执行也是没问题的,因为后面要用
        try {
            socket = new Socket("localhost", 10721);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 匿名，好用
            new Thread(new RecvMsg()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendSelf();

        // TODO 会有一个专门的线程负责接受服务端发来的消息，其中有一条消息负责显示当前在线成员

        // 初始化窗口
        initWindow();


    }

    /**
     * 把自己的信息发送给服务器
     */
    private void sendSelf() {
        // TODO 调用套接字的send，把自己的用户名和id发给服务器
        String sendMsg = "Self," + client_id + "," + client_name;
        System.out.println(sendMsg);
    }

    /**
     * 初始化窗口
     */
    private void initWindow() {
        setTitle("Client Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800, 600);

        // 创建左侧面板
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // 创建用户列表
        DefaultListModel<String> userListModel = new DefaultListModel<>();
        // TODO 这里的添加数据源就是接受来的消息要放置的地方
        userListModel.addElement("User 1");
        userListModel.addElement("User 2");
        userListModel.addElement("User 3");
        userListModel.addElement("User 4");
        userList = new JList<>(userListModel);
        userListScrollPane = new JScrollPane(userList);
        leftPanel.add(userListScrollPane);

        // 创建页面切换按钮
        privateChatButton = new JButton("Private Chat");
        groupChatButton = new JButton("Group Chat");
        changePasswordButton = new JButton("Change Password");
        leftPanel.add(privateChatButton);
        leftPanel.add(groupChatButton);
        leftPanel.add(changePasswordButton);

        // 创建右侧面板和卡片布局
        rightPanel = new JPanel();
        cardLayout = new CardLayout();
        rightPanel.setLayout(cardLayout);

        // 创建私聊页面
        privateChatPanel = new JPanel();
        privateChatPanel.setLayout(new BorderLayout());
        privateChatTextArea = new JTextArea();
        privateChatTextArea.setEditable(false);
        JScrollPane privateChatScrollPane = new JScrollPane(privateChatTextArea);
        JTextField privateChatUserIdTextField = new JTextField();
        JTextField privateChatMessageTextField = new JTextField();
        JButton privateChatSendButton = new JButton("Send");
        privateChatSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取发送对象和消息
                String userId = privateChatUserIdTextField.getText();
                String message = privateChatMessageTextField.getText();
                int id = Integer.parseInt(userId);
                // 调用函数发送
                SingleChat(id, message);
                // 输入框置空
                privateChatMessageTextField.setText("");
            }
        });
        JPanel privateChatInputPanel = new JPanel();
        privateChatInputPanel.setLayout(new BorderLayout());
        privateChatInputPanel.add(privateChatUserIdTextField, BorderLayout.NORTH);
        privateChatInputPanel.add(privateChatMessageTextField, BorderLayout.CENTER);
        privateChatInputPanel.add(privateChatSendButton, BorderLayout.SOUTH);
        privateChatPanel.add(privateChatScrollPane, BorderLayout.CENTER);
        privateChatPanel.add(privateChatInputPanel, BorderLayout.SOUTH);
        rightPanel.add(privateChatPanel, "Private Chat");

        // 创建群聊页面
        groupChatPanel = new JPanel();
        groupChatPanel.setLayout(new BorderLayout());
        groupChatTextArea = new JTextArea();
        groupChatTextArea.setEditable(false);
        JScrollPane groupChatScrollPane = new JScrollPane(groupChatTextArea);
        JTextField groupChatMessageTextField = new JTextField();
        JButton groupChatSendButton = new JButton("Send");
        groupChatSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取窗口输入的消息内容
                String message = groupChatMessageTextField.getText();
                GroupChat(message);
                // 置空输入框
                groupChatMessageTextField.setText("");
            }
        });
        JPanel groupChatInputPanel = new JPanel();
        groupChatInputPanel.setLayout(new BorderLayout());
        groupChatInputPanel.add(groupChatMessageTextField, BorderLayout.CENTER);
        groupChatInputPanel.add(groupChatSendButton, BorderLayout.EAST);
        groupChatPanel.add(groupChatScrollPane, BorderLayout.CENTER);
        groupChatPanel.add(groupChatInputPanel, BorderLayout.SOUTH);
        rightPanel.add(groupChatPanel, "Group Chat");

        // 创建修改密码页面
        changePasswordPanel = new JPanel();
        changePasswordPanel.setLayout(new BorderLayout());
        JTextField oldPasswordTextField = new JTextField();
        JTextField newPasswordTextField = new JTextField();
        JButton confirmChangePasswordButton = new JButton("Change Password");
        confirmChangePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取输入的密码信息
                String oldPassword = oldPasswordTextField.getText();
                String newPassword = newPasswordTextField.getText();
                ChangePwd(oldPassword, newPassword);
                // 置空输入框
                oldPasswordTextField.setText("");
                newPasswordTextField.setText("");
            }
        });
        JPanel changePasswordInputPanel = new JPanel();
        changePasswordInputPanel.setLayout(new GridLayout(2, 2));
        changePasswordInputPanel.add(new JLabel("Old Password:"));
        changePasswordInputPanel.add(oldPasswordTextField);
        changePasswordInputPanel.add(new JLabel("New Password:"));
        changePasswordInputPanel.add(newPasswordTextField);
        changePasswordPanel.add(changePasswordInputPanel, BorderLayout.CENTER);
        changePasswordPanel.add(confirmChangePasswordButton, BorderLayout.SOUTH);
        rightPanel.add(changePasswordPanel, "Change Password");

        // 将左侧和右侧面板添加到窗口中
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // 监听页面切换按钮的点击事件
        privateChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(rightPanel, "Private Chat");
            }
        });
        groupChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(rightPanel, "Group Chat");
            }
        });
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("修改密码页面");
                cardLayout.show(rightPanel, "Change Password");
            }
        });
    }

    /**
     * 私聊
     * @param userID：私聊对象
     * @param message：私聊要发送的消息
     */
    private void SingleChat(int userID, String message) {
        System.out.println(client_name + "给[" + userID + "]说：" + message);

        String sendMsg = "Single," + userID + "," + message;
        try {
            writer.write(sendMsg);
            writer.newLine();
            writer.flush();
            privateChatTextArea.append("\t\t\t" + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群聊
     * @param message 群聊发送的消息
     */
    private void GroupChat(String message) {
        System.out.println(client_name + "说了：" + message);

        String sendMsg = "Group," + message + ",nil";
        try {
            writer.write(sendMsg);
            writer.newLine();
            writer.flush();
            groupChatTextArea.append("\t\t\t" + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改密码
     * @param pwd：要修改的新密码
     */
    private void ChangePwd(String oldPwd, String pwd) {                 // TODO 修改密码的业务功能之后再完善
        System.out.println("将要修改的密码是：" + pwd);
        String sendMsg = "Change," + oldPwd + "," + pwd;
        // TODO 调用套接字的send
        System.out.println(sendMsg);
    }

    /**
     * 接收消息线程
     */
    private class RecvMsg implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    // 会接收到三种类型消息：
                    // 1、Signal：私聊，需要判断ID是否是自己，不是忽略，是了显示
                    // 2、Group：直接显示，并且添加到数据库消息记录中
                    // 3、Online：当前在线用户信息，需要显示到ListView中
                    String[] strs = message.split(",");
                    if(strs[0].equals("Signal")) {
                        privateChatTextArea.append("[" + strs[1] + "]: " + strs[2] + "\n");
                    } else if(strs[0].equals("Group")) {
                        groupChatTextArea.append("[" + strs[1] + "]: " + strs[2] + "\n");
                    } else if(strs[0].equals("Online")) {
                        DefaultListModel<String> userListModel = new DefaultListModel<>();
                        for (int i = 0; i < strs.length; i++) {
                            userListModel.addElement(strs[i]);
                        }
                        userList = new JList<>(userListModel);
                        userListScrollPane.setViewportView(userList);
                    } else {
                        System.out.println("接受到的消息有误，程序无法正常执行...");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


















    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ShineSpotClient window = new ShineSpotClient("kuuhaku");
                window.setVisible(true);
            }
        });
    }
}

