package SSProject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 登录窗口
 * 1、获取数据库连接对象
 * 2、查询已存在的用户名和密码，保存在缓存中
 * 3、创建窗口，等待用户输入
 * 4、验证用户输入是否正确
 * 5、continue;
 */
public class LoginWindow extends JFrame {
    private MySQLHelper helper;
    private Map<String, String> userInfo;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private String accessName;
    public boolean allow;

    public LoginWindow() throws Exception {
        // 设置窗口标题
        super("Login Window");

        // 读取用户信息
        helper = new MySQLHelper();
        userInfo = helper.readUserTable();

        // 初始化窗口
        allow = false;
        initWindow();
    }
    /**
     * 初始化窗口
     */
    private void initWindow() {
        setSize(520, 305);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建面板
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // 创建用户名标签和文本框
        JLabel usernameLabel = new JLabel("Username:");
        Font font = new Font("Arial", Font.BOLD, 17);
        usernameLabel.setFont(font);
        usernameLabel.setForeground(Color.RED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(usernameLabel, gbc);

        usernameTextField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(usernameTextField, gbc);

        // 创建密码标签和密码框
        JLabel passwordLabel = new JLabel("Password:");
        font = new Font("Arial", Font.BOLD, 17);
        passwordLabel.setFont(font);
        passwordLabel.setForeground(Color.RED);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(passwordField, gbc);

        // 创建登录按钮
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        // 添加面板到窗口中
        setContentPane(panel);

        // 添加登录按钮的点击事件监听器
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                String password = new String(passwordField.getPassword());

                if(userInfo.containsKey(username)) {
                    if(userInfo.get(username).equals(password)) {
                        // 验证通过，关闭当前窗口并显示新窗口
                        accessName = username;
                        JOptionPane.showMessageDialog(LoginWindow.this, "登录成功, 欢迎: " + accessName, "success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        ShowMainWindow();
                    } else {
                        // 验证失败，弹出提示错误信息的对话框
                        JOptionPane.showMessageDialog(LoginWindow.this, "用户名或密码错误", "Fatal Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // 验证失败，弹出提示错误信息的对话框
                    JOptionPane.showMessageDialog(LoginWindow.this, "用户名或密码错误", "Fatal Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 显示窗口
        setVisible(true);
    }

    /**
     * 显示主窗口
     */
    private void ShowMainWindow() {
        ShineSpotClient client = new ShineSpotClient(accessName);
        client.setVisible(true);
    }

}
