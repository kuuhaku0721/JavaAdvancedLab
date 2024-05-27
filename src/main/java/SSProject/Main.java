package SSProject;

public class Main {
    /**
     * 启动方法：先到Server类中启动服务器，然后到这里启动客户端
     * 启动成功后会出现两个登录窗口，各自登录之后就可以进行聊天等业务了
     */
    public static void main(String[] args) throws Exception {
        /**
         * 启动两个客户端窗口，用同一个控制台，同生共死
         * 因为写在了构造函数里面，所以只需要实例化对象即可
         */
        LoginWindow login1 = new LoginWindow();
        LoginWindow login2 = new LoginWindow();
    }
}
