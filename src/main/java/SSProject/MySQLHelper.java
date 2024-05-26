package SSProject;


import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库帮助类
 * 主要功能包括：
 * 1、使用《实验：IO和XML》读取xml文件中的数据库配置信息，连接数据库，建立JDBC连接
 * 2、对外提供接口，负责增删改查功能
 *      增删改查包括：
 *         查：创建实例化对象之后，根据群聊或者私聊的类型不同返回对应的聊天记录信息
 *         增：在每一个发送消息的函数里面调用helper的接口，负责往数据库里面写入数据（需要单独开线程去完成，或许可以使用lambda）
 * 3、关闭连接的接口
 */

public class MySQLHelper {
    private Connection conn;

    public MySQLHelper() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        // TODO 之后这部分换成读取xml文件来写入配置信息
        String url = "jdbc:mysql://127.0.0.1:3306/kuudb";
        String username = "kuuhaku";
        String password = "002016";
        conn = DriverManager.getConnection(url, username, password);
    }
    /**
     * 读取用户账号信息
     * @return key:用户名，value:密码
     */
    public Map<String, String> readUserTable() {
        Map<String, String> userMap = new HashMap<>();

        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM user";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String username = rs.getString("uname");
                String password = rs.getString("password");
                userMap.put(username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userMap;
    }

    /**
     * 执行SQL语句
     * @param sql: 要执行的sql语句
     */
    public void executeSQL(String sql) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.err.println("SQL 语句执行失败: " + e.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("关闭 Statement 失败: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 测试用main函数
     */
    public static void main(String[] args) throws Exception {
        MySQLHelper helper = new MySQLHelper();
        Map<String, String> map = helper.readUserTable();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("Username: " + entry.getKey() + ", Password: " + entry.getValue());
        }
    }
}
