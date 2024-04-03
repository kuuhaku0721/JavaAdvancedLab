package lab;


/**
 * (1) 在MySQL(你总不能指望我用Redis吧)中定义一个过程和一个函数，使用JDBC调用他们。这个过程/函数可以是计算一个数学公式，也可以访问一张表。（基础） (clear)
 * (2) 在MySQL中创建一张表，至少包含一个可存储大文本的字段和一个可存储二进制数据的字段，用JDBC写入一篇文章和一幅图像，并读出他们。（基础） (clear)
 */

import java.io.*;
import java.sql.*;

/**
 * SQL的加载驱动之类的吧，之前Web课做过，直接捞一个来用就行了
 */
class LargeDataTable {
    private Connection conn;
    private InputStream inputStream;
    private OutputStream outputStream;
    public LargeDataTable() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/your_database_name";   // 先把这里的信息改成你自己的
        String username = "your_username";
        String password = "your_pwd";
        conn = DriverManager.getConnection(url, username, password);
    }
    // 把一二的顺序颠倒一下，先准备表，然后再调用存储过程和函数，这样在调用的时候就可以用上这个表了
    // 建表
    public void CreateTable() throws Exception {
        if (!isTableExists(conn, "large_data_table")) {
            Statement createTableStatement = conn.createStatement();
            createTableStatement.executeUpdate("CREATE TABLE large_data_table (id INT AUTO_INCREMENT PRIMARY KEY, text_data TEXT, image_data LONGBLOB)");
        }
    }
    // 检查表是否存在
    private boolean isTableExists(Connection connection, String tableName) throws Exception {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }
    // 插入
    public void InsertToTable() throws Exception {
        // 不提供传参版本，如果想修改传入内容，请自行手动更改(多余的东西没必要做，好钢用在刀刃上，时间用在更有意义的事情上)
        // 插入文本数据
        String text = "This is a large text.如果你觉得需要，之后找个文本文件，读取文件，转成字符串，然后放进去就行了。文件的上限取决于MySQL的TEXT类型的上限";
        // 插入图像数据
        File imageFile = new File("src/main/resources/img/homura.png");
        FileInputStream fis = new FileInputStream(imageFile);

        PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO large_data_table (text_data, image_data) VALUES (?, ?);");
        insertStatement.setString(1, text);
        insertStatement.setBinaryStream(2, fis, (int)imageFile.length());
        insertStatement.executeUpdate();
    }
    // 读取
    public void ReadFromTable() throws Exception {
        // 读取文本和图像数据
        Statement selectStatement = conn.createStatement();
        ResultSet resultSet = selectStatement.executeQuery("SELECT * FROM large_data_table");
        while (resultSet.next()) {
            System.out.println("读取到的文本数据如下: ");
            System.out.println("TEXT: " + resultSet.getString("text_data"));
            System.out.println("读取到的二进制数据已转存到本地...");

            inputStream = resultSet.getBinaryStream("image_data");
            if(inputStream != null){
                outputStream = new FileOutputStream("src/main/resources/img/output_image_backup.png");

                byte[] buffer = new byte[40960]; // 4MB
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }else{
                System.out.println("输入流是个空的");
            }
        }
        inputStream.close();
        outputStream.close();
    }
    // 调用存储过程和函数
    public void CallFunction() throws Exception{
        // 调用存储过程
        CallableStatement callStatement = conn.prepareCall("{call read_all_largedata()}");
        ResultSet resultSet = callStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println("读取到的文本数据如下: ");
            System.out.println("TEXT: " + resultSet.getString("text_data"));
            System.out.println("也读取到了二进制数据，但是不想重复输出了");
        }

        // 调用函数
        callStatement = conn.prepareCall("{? = call calculate_from_largedata()}");    // 注意这里没有参数，因为在MySQL里面定义的时候就没有设置参数
        callStatement.registerOutParameter(1, Types.INTEGER);
        callStatement.execute();
        int result = callStatement.getInt(1);
        System.out.println("调用函数，计算当前id总和，结果为: " + result);
    }
}

/**
 * 这里是存储过程和函数的定义语句
 * 存储过程：
 * BEGIN
 * 	#Routine body goes here...
 * 		SELECT * FROM large_data_table;
 * END
 * 函数：
 * BEGIN
 * 	#Routine body goes here...
 * 	DECLARE total_id INT;
 * 	SELECT SUM(id) INTO total_id FROM large_data_table;
 * 	RETURN total_id;
 * END
 */

public class Lab010 {
//    public static void main(String[] args) throws Exception {
//        LargeDataTable lab = new LargeDataTable();
//        lab.CreateTable();
//        lab.InsertToTable();
//        lab.ReadFromTable();
//        lab.CallFunction();
//    }

    public void RunLab010() throws Exception {
        LargeDataTable lab = new LargeDataTable();
        lab.CreateTable();
        lab.InsertToTable();
        lab.ReadFromTable();
        lab.CallFunction();
    }

}