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


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ShineSpotClient extends JFrame {
    private JPanel rightPanel;
    private CardLayout cardLayout;
    private JTextArea privateChatTextArea;
    private JTextArea groupChatTextArea;
    private JScrollPane userListScrollPane;

    private String client_name;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ShineSpotClient(String name) {
        // 初始化客户端信息
        client_name = name;

        // 初始化窗口
        initWindow();

        // 连接服务器，并把自己信息发送给服务器
        try {
            socket = new Socket("localhost", 10721);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            new Thread(new RecvMsg()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendSelf();
    }

    /**
     * 把自己的信息发送给服务器
     */
    private void sendSelf() {
        String sendMsg = "Self," + client_name;
        System.out.println(sendMsg);
        try {
            writer.write(sendMsg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化窗口
     */
    private void initWindow() {
        setTitle(client_name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800, 600);

        // 创建左侧面板
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // 创建用户列表
        userListScrollPane = new JScrollPane();
        leftPanel.add(userListScrollPane);

        // 创建页面切换按钮
        JButton privateChatButton = new JButton("Private Chat");
        JButton groupChatButton = new JButton("Group Chat");
        JButton changePasswordButton = new JButton("Change Password");
        leftPanel.add(privateChatButton);
        leftPanel.add(groupChatButton);
        leftPanel.add(changePasswordButton);

        // 创建右侧面板和卡片布局
        rightPanel = new JPanel();
        cardLayout = new CardLayout();
        rightPanel.setLayout(cardLayout);

        // 创建私聊页面
        JPanel privateChatPanel = new JPanel();
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
                String targetName = privateChatUserIdTextField.getText();
                String message = privateChatMessageTextField.getText();
                // 调用函数发送
                SingleChat(targetName, message);
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
        JPanel groupChatPanel = new JPanel();
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
        JPanel changePasswordPanel = new JPanel();
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
     * @param targetName：私聊对象
     * @param message：私聊要发送的消息
     */
    private void SingleChat(String targetName, String message) {
        // System.out.println(client_name + "给[" + targetName + "]说：" + message);

        // Single,sender,receiver,msg
        String sendMsg = "Single," + client_name + "," + targetName + "," + message;
        try {
            writer.write(sendMsg);
            writer.newLine();
            writer.flush();
            privateChatTextArea.append("\t\t\t\t\t" + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群聊
     * @param message 群聊发送的消息
     */
    private void GroupChat(String message) {
        // System.out.println(client_name + "说了：" + message);

        String sendMsg = "Group," + client_name + ",nil," + message;
        try {
            writer.write(sendMsg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改密码
     * @param newPwd：要修改的新密码
     */
    private void ChangePwd(String oldPwd, String newPwd) {
        String sendMsg = "Change," + client_name + "," + oldPwd + "," + newPwd;
        // System.out.println("将要发送出去的：" + sendMsg);
        try {
            writer.write(sendMsg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收消息线程
     */
    private class RecvMsg implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    /** 修改密码结果提示 成功为success，失败为error **/
                    if(message.equals("update error")) {
                        JOptionPane.showMessageDialog(ShineSpotClient.this,
                                "修改密码失败\n用户名或密码错误", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                    if(message.equals("update success")) {
                        JOptionPane.showMessageDialog(ShineSpotClient.this,
                                "修改密码成功", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    }
                    /** 处理接受到的消息，根据不同的消息类型执行不同的处理 **/
                    String[] strs = message.split(",");
                    if(!strs[0].equals("Online") && strs.length < 4) continue;
                    switch (strs[0]) {
                        case "Single" -> {
                            String msg = "nil";
                            if (strs[2].equals(client_name)) {
                                msg = "[" + strs[1] + "]: " + strs[3] + "\n";
                            }
                            if (!msg.equals("nil"))
                                privateChatTextArea.append(msg);
                            // System.out.println("收到私聊：" + msg);
                        }
                        case "Group" -> {
                            String msg = "[" + strs[1] + "]: " + strs[3] + "\n";
                            groupChatTextArea.append(msg);
                            // System.out.println("收到群聊: " + msg);
                        }
                        case "Online" -> {
                            DefaultListModel<String> userModel = new DefaultListModel<>();
                            for (int i = 1; i < strs.length; i++) {
                                if (!strs[i].equals(""))
                                    userModel.addElement(strs[i]);
                            }
                            JList<String> userList = new JList<>(userModel);
                            userListScrollPane.setViewportView(userList);
                        }
                        default -> {
                            System.out.println("client--接受到的消息有误，程序无法正常执行...");
                            System.out.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
