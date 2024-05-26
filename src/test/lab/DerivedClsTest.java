package lab;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * 用法：找到想要测试的类，选中，Alt+Enter，选择 Create Test
 *      然后最上面选 JUnit4  下面方法全选 然后选择一个目录位置
 * 之后会看到生成了一些测试用例，稍微修改一下加点东西进去，然后运行就行了
 * 运行：往右上角看，有个绿色的箭头，左侧下拉框选中Current File 右边按钮第三个 鼠标放上去显示 Run "xxx.java" with Coverage
 *      然后直接运行之后就能看到结果了
 * 或者，如果你和我一样用的Maven架构的话
 * 找到你的测试类的文件，右键--Run Maven--Test xxxx(类名)，然后就能看到结果了
 */
public class DerivedClsTest {
    // 2、测试前输出和测试后输出
    @BeforeClass
    public static void setUpClass() {
        System.out.println("开始测试...");
    }
    @AfterClass
    public static void tearDownClass() {
        System.out.println("测试结束.");
    }

    // 下面的可以自动生成，不用手写
    // 1、测试所有方法
    // 3、在每个方法测试前输出方法名，结束后输出“xxx方法测试结束”，xxx为方法名
    @Test
    public void beOverride() {
        System.out.println("beOverride 方法测试开始...");
        DerivedCls derivedCls = new DerivedCls();
        derivedCls.beOverride();
        System.out.println("beOverride 方法测试结束。");
    }

    @Test
    public void suppress_warn() {
        System.out.println("suppress_warn 方法测试开始...");
        DerivedCls derivedCls = new DerivedCls();
        derivedCls.suppress_warn();
        System.out.println("suppress_warn 方法测试结束。");
    }

    @Test
    public void testUncheckedWarning() {
        System.out.println("testUncheckedWarning 方法测试开始...");
        DerivedCls derivedCls = new DerivedCls();
        derivedCls.testUncheckedWarning();
        System.out.println("testUncheckedWarning 方法测试结束。");
    }
    @Test
    public void testRawtypesWarning() {
        System.out.println("testRawtypesWarning 方法测试开始...");
        DerivedCls derivedCls = new DerivedCls();
        derivedCls.testRawtypesWarning();
        System.out.println("testRawtypesWarning 方法测试结束。");
    }
    // 父类的和未实现的方法
    @Test
    public void testPrint() {
        System.out.println("testprint 方法测试开始...");
        DerivedCls derivedCls = new DerivedCls();
        derivedCls.print();
        System.out.println("testprint 方法测试结束。");
    }
    // 4、使用@Ignore跳过未实现方法的测试
    @Ignore("未实现的方法，跳过测试")
    @Test
    public void sendToLab08() {
    }
}