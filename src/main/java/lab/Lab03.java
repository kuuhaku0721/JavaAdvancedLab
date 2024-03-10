package lab;


import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

/**
 * (1) 创建一个文本文件，使用缓冲流写入若干字符，并读出显示； (clear)
 *     创建一个二进制文件和一个对象（包含若干个类型为整数、浮点数或字符串的属性），使用数据流写入若干个对象实例，并读出显示。（基础） (clear)
 * (2) 使用DOM解析一个简单的XML文件。   (clear)
 *     要求XML文件至少包含两层元素，同层元素有兄弟节点，部分元素包含属性。
 */

class People implements Serializable{
    private int age;            // 整数型
    private float threesize;   // 浮点数型
    private String name;        // 字符串型

    public People(int age, float threesize, String name) {
        this.age = age;
        this.threesize = threesize;
        this.name = name;
    }

    @Override
    public String toString() {
        return "People{" +
                "age=" + age +
                ", score=" + threesize +
                ", name='" + name + '\'' +
                '}';
    }
}

public class Lab03 {
    /**
     * 创建一个文本文件，使用缓冲流写入若干字符，并读出显示
     */
    private void ReadWriteExample() {
        System.out.println("-----------------创建文本文件-----------------");
        String str = "Hello, world\nversion lab03";

        System.out.println("写入文本文件: " + str);
        WriteToFile("test.txt", str);

        String content = ReadFromFile("test.txt");
        System.out.println("读取文件文本内容:" + content);
    }
    // 写入文本文件
    private void WriteToFile(String fileName, String text) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            if(writer == null) System.out.println("writer创建失败");
            writer.write(text);
            writer.close();
            System.out.println("文件写入完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 读取文本文件内容
    private String ReadFromFile(String fileName) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 创建一个二进制文件和一个对象（包含若干个类型为整数、浮点数或字符串的属性），使用数据流写入若干个对象实例，并读出显示
     */
    private void BinaryFile(){
        // 创建对象
        People kiana = new People(16, 8459.87f, "琪亚娜·卡斯兰娜");
        People mei = new People(17, 8962.90f, "雷电芽衣");
        People bronya = new People(14, 7455.76f, "布洛妮娅·扎伊切克");

        // 写入二进制文件
        WriteBinaryFile("test.bin", kiana, mei, bronya);

        // 读取并显示二进制文件内容
        ReadBinaryFile("test.bin");
    }
    // 写入二进制文件
    private void WriteBinaryFile(String fileName, People... objects) {
        try  {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            for (People obj : objects) {
                outputStream.writeObject(obj);
            }
            // 关闭流
            outputStream.close();
            System.out.println("对象写入完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 读取二进制文件内容
    private void ReadBinaryFile(String fileName) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
            Object obj;
            while ((obj = inputStream.readObject()) != null) {
                if (obj instanceof People) {
                    People people = (People) obj;
                    System.out.println("读取到: " + people);
                }
            }
            inputStream.close();
        } catch(EOFException e){
            // nothing 碰到文件尾了 不处理就行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用DOM解析一个简单的XML文件
     * 直接拿pom.xml用，反正它啥都有
     */
    private void ParseXMLFile() {
        String fileName = "pom.xml";

        try {
            File xmlFile = new File(fileName);
            // 从文件创建一个文档对象，之后会对这个document对象进行解析
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            document.getDocumentElement().normalize();
            System.out.println("Root element: " + document.getDocumentElement().getNodeName());

            // 想象一个树的形状，它的每一个<>就是树上的一个节点，相互嵌套的<>就是父子节点
            NodeList nodeList = document.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                // 这里可以联想一下C#里面，创建窗口时用的xaml，它也有类似<PushButton Name=""> 这些内容
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println("Element: " + element.getNodeName());

                    NamedNodeMap attributes = element.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attribute = attributes.item(j);
                        System.out.println("Attribute: " + attribute.getNodeName() + " = " + attribute.getNodeValue());
                    }

                    // 它有子节点的情况就更容易想象了，<GroupBox>里面也是要放子节点的吧
                    if (element.hasChildNodes()) {
                        NodeList children = element.getChildNodes();
                        // 这里输出的是<h1>...</h1>中间的内容
                        for (int k = 0; k < children.getLength(); k++) {
                            Node child = children.item(k);
                            if (child.getNodeType() == Node.TEXT_NODE) {
                                System.out.println("Text: " + child.getNodeValue().trim());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void RunLab03() {
        ReadWriteExample();

        BinaryFile();

        ParseXMLFile();

    }
}
