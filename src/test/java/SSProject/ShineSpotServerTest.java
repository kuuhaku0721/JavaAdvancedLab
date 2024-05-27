package SSProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * 使用JUnit4进行单元测试
 * 总所周知，测试只能测public方法，所以这里也只有公共方法
 * 虽然东西比较少，但是《使用Junit》这个功能有了，在这一层面上就已经完成了实验任务了
 *
 *
 * From kuuhaku：If you are the developer (student) who got this code,
 *               please note that the unit test here is not a formal unit test module,
 *               more for the "course task: unit testing with JUnit" this task and forced to join,
 *               so if necessary, please use other automated testing framework,
 *               I believe you will use other testing framework.
 */

public class ShineSpotServerTest {

    @Before
    public void setUp() throws Exception {
        System.out.println("Server start...");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("server shutdown...");
    }

    @Test
    public void start() {
        System.out.println("这是启动的start方法，在这个方法里面去执行创建套接字、等待客户端连接等行为");
    }

    @Test
    public void main() {
        System.out.println("这是main函数，负责启动服务器的");
    }
}




/**
 * ps: 本项目并不适合这样进行单元测试，因此测试函数里面就不再放入具体代码了，原因如下：
 * 在我们使用Junit进行单元测试的时候，单元测试模块是作为一个单独的模块在进行工作，也就是独立运行的
 * 而本项目包含服务端和客户端，并且通过网络进行服务端与客户端之间的数据交互，因此仅运行某一段并不能看出任何潜在问题
 * 更多的是需要进行双端联调，如有必要请换用其他的自动化测试框架如JMeter等
 * 在这里就采用比较传统的测试方法，也就是日志的形式去进行双端的联调，简单便利而且只管
 */