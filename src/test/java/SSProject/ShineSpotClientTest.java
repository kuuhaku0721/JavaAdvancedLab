package SSProject;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;

public class ShineSpotClientTest {

    @Before
    public void setUp() throws Exception {
        System.out.println("这里是客户端模块的单元测试函数");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("客户端测试模块执行完毕执行");
    }
}