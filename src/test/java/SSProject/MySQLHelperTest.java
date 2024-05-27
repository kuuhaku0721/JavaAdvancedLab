package SSProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * 数据库的单元测试模块
 */

public class MySQLHelperTest {

    private Connection conn;
    private String dbname;
    private String sqlname;
    private String sqlpwd;
    @Before
    public void setUp() throws Exception {
        System.out.println("测试模块启动前执行...");
        // 加载驱动
        Class.forName("com.mysql.jdbc.Driver");
        // 读取xml文件读取出来配置信息
        ReadFromXML();
        // 连接数据库
        String url = "jdbc:mysql://127.0.0.1:3306/" + dbname;
        String username = sqlname;
        String password = sqlpwd;
        conn = DriverManager.getConnection(url, username, password);
    }
    private void ReadFromXML() {
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

    @After
    public void tearDown() throws Exception {
        System.out.println("测试模块执行完毕执行...");
    }

    @Test
    public void readUserTable() {
        Map<String, String> userMap = new HashMap<>();
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM user";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String username = rs.getString("uname");
                String password = rs.getString("password");
                userMap.put(username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void executeSQL() {
        String sql = "SELECT * FROM user";
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

    @Test
    public void main() {
        System.out.println("测试用的启动函数，没有实际作用");
    }
}