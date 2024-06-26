package SSProject;


import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库帮助类
 * 主要功能包括：
 * 1、读取xml文件中的数据库配置信息，连接数据库，建立JDBC连接
 * 2、对外提供接口，负责增删改查功能，查询数据库用户信息，实现密码修改等
 * 3、实现自定义注解负责SQL语句的写入
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface SQLQuery {
    String value();
}
public class MySQLHelper {
    private Connection conn;
    private String dbname;
    private String sqlname;
    private String sqlpwd;

    public MySQLHelper() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        // 读取xml文件读取出来配置信息
        ReadFromXML();

        // 使用读取文件出来的信息，连接数据库
        String url = "jdbc:mysql://127.0.0.1:3306/" + dbname;
        String username = sqlname;
        String password = sqlpwd;
        conn = DriverManager.getConnection(url, username, password);
    }

    /**
     * 解析XML文件
     * 从文件中读取出来数据库用户名和密码
     */
    private void ReadFromXML() {
        // 文件路径，在项目路径下
        String fileName = "src/main/java/SSProject/sqlsettings.xml";
        try {
            File xmlFile = new File(fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();
            // System.out.println("Root element: " + document.getDocumentElement().getNodeName());
            NodeList nodeList = document.getElementsByTagName("*");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // System.out.println("Element: " + element.getNodeName());
                    if (element.hasChildNodes()) {
                        NodeList children = element.getChildNodes();
                        Node child = children.item(0);
                        if (child.getNodeType() == Node.TEXT_NODE) {
                            switch (element.getNodeName()) {
                                case "database" -> {
                                    // System.out.println("database: " + child.getNodeValue().trim());
                                    dbname = child.getNodeValue().trim();
                                }
                                case "username" -> {
                                    // System.out.println("username: " + child.getNodeValue().trim());
                                    sqlname = child.getNodeValue().trim();
                                }
                                case "password" -> {
                                    // System.out.println("password: " + child.getNodeValue().trim());
                                    sqlpwd = child.getNodeValue().trim();
                                }
                                default -> {
                                    System.out.println("默认的");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取用户账号信息
     * @return key:用户名，value:密码
     */
    @SQLQuery("SELECT * FROM user")
    public Map<String, String> readUserTable() {
        // 使用反射获取注解并执行对应的sql语句并执行
        Method method = null;
        try {
            method = this.getClass().getDeclaredMethod("readUserTable");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        SQLQuery annotation = method.getAnnotation(SQLQuery.class);
        String sql = annotation.value();

        // 反射拿到sql语句之后，再调用jdbc的查询
        Map<String, String> userMap = new HashMap<>();
        try (Statement stmt = conn.createStatement()) {
            // String sql = "SELECT * FROM user";
            ResultSet rs = stmt.executeQuery(sql);
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

    }
}
