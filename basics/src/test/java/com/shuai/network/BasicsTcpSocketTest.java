package com.shuai.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TCP Socket 测试类
 */
@DisplayName("TCP Socket 测试")
class BasicsTcpSocketTest {

    @Test
    @DisplayName("TCP 基本概念测试")
    void tcpConceptTest() {
        System.out.println("  TCP (Transmission Control Protocol) 特点:");
        System.out.println("    - 面向连接：三次握手建立连接");
        System.out.println("    - 可靠传输：确认、重传、排序");
        System.out.println("    - 流量控制：滑动窗口");
        System.out.println("    - 拥塞控制：慢启动、拥塞避免");
        System.out.println("");
        System.out.println("  TCP 三次握手:");
        System.out.println("    1. 客户端 -> SYN -> 服务器");
        System.out.println("    2. 服务器 -> SYN+ACK -> 客户端");
        System.out.println("    3. 客户端 -> ACK -> 服务器");
        assertTrue(true);
    }

    @Test
    @DisplayName("ServerSocket 基本用法")
    void serverSocketTest() {
        System.out.println("  ServerSocket 常用方法:");
        System.out.println("    new ServerSocket(port)           - 创建服务器套接字");
        System.out.println("    accept()                         - 等待客户端连接（阻塞）");
        System.out.println("    getLocalPort()                   - 获取本地端口");
        System.out.println("    setSoTimeout(timeout)            - 设置 accept 超时");
        System.out.println("    close()                          - 关闭套接字");
        System.out.println("");
        System.out.println("  服务器端模板:");
        System.out.println("    try (ServerSocket server = new ServerSocket(8080)) {");
        System.out.println("        Socket client = server.accept();");
        System.out.println("        // 处理客户端连接");
        System.out.println("    }");
        assertTrue(true);
    }

    @Test
    @DisplayName("Socket 客户端用法")
    void socketClientTest() {
        System.out.println("  Socket 常用方法:");
        System.out.println("    new Socket(host, port)           - 创建客户端套接字");
        System.out.println("    getInputStream()                 - 获取输入流");
        System.out.println("    getOutputStream()                - 获取输出流");
        System.out.println("    getLocalAddress()                - 获取本地地址");
        System.out.println("    getPort()                        - 获取远程端口");
        System.out.println("    isConnected()                    - 是否已连接");
        System.out.println("    close()                          - 关闭套接字");
        System.out.println("");
        System.out.println("  客户端模板:");
        System.out.println("    try (Socket socket = new Socket(\"localhost\", 8080)) {");
        System.out.println("        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);");
        System.out.println("        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));");
        System.out.println("        out.println(\"Hello\");");
        System.out.println("        String response = in.readLine();");
        System.out.println("    }");
        assertTrue(true);
    }

    @Test
    @DisplayName("TCP 数据读写测试")
    void tcpDataTransferTest() {
        System.out.println("  TCP 数据读写方式:");
        System.out.println("");
        System.out.println("  方式1：使用 BufferedReader + PrintWriter");
        System.out.println("    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));");
        System.out.println("    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);");
        System.out.println("    String line = in.readLine();");
        System.out.println("    out.println(\"Echo: \" + line);");
        System.out.println("");
        System.out.println("  方式2：使用 DataInputStream + DataOutputStream");
        System.out.println("    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());");
        System.out.println("    DataInputStream dis = new DataInputStream(socket.getInputStream());");
        System.out.println("    dos.writeUTF(message);");
        System.out.println("    String msg = dis.readUTF();");
        System.out.println("");
        System.out.println("  方式3：使用 ObjectInputStream + ObjectOutputStream（需处理序列化）");
        assertTrue(true);
    }

    @Test
    @DisplayName("TCP Echo 服务器示例")
    void tcpEchoServerTest() {
        System.out.println("  Echo 服务器原理：");
        System.out.println("    1. 监听指定端口");
        System.out.println("    2. 接受客户端连接");
        System.out.println("    3. 读取客户端消息");
        System.out.println("    4. 将消息回传给客户端");
        System.out.println("    5. 重复步骤 3-4，直到客户端断开");
        System.out.println("    6. 关闭连接，继续监听");
        System.out.println("");
        System.out.println("  代码模板:");
        System.out.println("    try (ServerSocket server = new ServerSocket(8888)) {");
        System.out.println("        while (true) {");
        System.out.println("            Socket client = server.accept();");
        System.out.println("            new Thread(() -> handleClient(client)).start();");
        System.out.println("        }");
        System.out.println("    }");
        assertTrue(true);
    }

    @Test
    @DisplayName("TCP 粘包问题测试")
    void tcpStickyPacketTest() {
        System.out.println("  TCP 粘包/拆包问题:");
        System.out.println("    - TCP 是流协议，没有消息边界");
        System.out.println("    - 多次 send 可能被合并（粘包）");
        System.out.println("    - 一次 send 可能被拆分（拆包）");
        System.out.println("");
        System.out.println("  解决方案：");
        System.out.println("    1. 定长消息：不足补空格");
        System.out.println("    2. 特殊分隔符：如 \\r\\n");
        System.out.println("    3. 长度+内容：前 4 字节表示长度");
        System.out.println("");
        System.out.println("  长度+内容示例:");
        System.out.println("    // 发送端");
        System.out.println("    byte[] data = message.getBytes();");
        System.out.println("    dos.writeInt(data.length);");
        System.out.println("    dos.write(data);");
        System.out.println("");
        System.out.println("    // 接收端");
        System.out.println("    int len = dis.readInt();");
        System.out.println("    byte[] data = new byte[len];");
        System.out.println("    dis.readFully(data);");
        assertTrue(true);
    }

    @Test
    @DisplayName("TCP 心跳机制测试")
    void tcpHeartbeatTest() {
        System.out.println("  TCP Keep-Alive 机制:");
        System.out.println("    - 检测连接是否存活");
        System.out.println("    - 防止连接被防火墙关闭");
        System.out.println("    - 默认 2 小时检测一次");
        System.out.println("");
        System.out.println("  应用层心跳（推荐）:");
        System.out.println("    // 定时发送心跳包");
        System.out.println("    out.println(\"PING\");");
        System.out.println("    String response = in.readLine();");
        System.out.println("    if (\"PONG\".equals(response)) { /* 连接正常 */ }");
        System.out.println("");
        System.out.println("  Socket 配置:");
        System.out.println("    socket.setKeepAlive(true);              // 开启 TCP Keep-Alive");
        System.out.println("    socket.setSoTimeout(30000);             // 设置读写超时");
        System.out.println("    socket.setTcpNoDelay(true);             // 禁用 Nagle 算法（低延迟）");
        assertTrue(true);
    }

    @Test
    @DisplayName("TCP 异常处理测试")
    void tcpExceptionTest() {
        System.out.println("  TCP 常见异常:");
        System.out.println("    - ConnectionRefusedException: 连接被拒绝");
        System.out.println("    - SocketTimeoutException: 连接/读写超时");
        System.out.println("    - SocketException: socket 错误（断开等）");
        System.out.println("    - IOException: I/O 错误");
        System.out.println("");
        System.out.println("  异常处理示例:");
        System.out.println("    try {");
        System.out.println("        // 连接和读写");
        System.out.println("    } catch (ConnectException e) {");
        System.out.println("        // 服务器不可达");
        System.out.println("    } catch (SocketTimeoutException e) {");
        System.out.println("        // 超时");
        System.out.println("    } catch (IOException e) {");
        System.out.println("        // 其他 I/O 错误");
        System.out.println("    }");
        assertTrue(true);
    }

    @Test
    @DisplayName("TCP NIO 通道测试")
    void tcpNioChannelTest() {
        System.out.println("  TCP NIO 组件:");
        System.out.println("    - ServerSocketChannel: 服务器通道");
        System.out.println("    - SocketChannel: 客户端通道");
        System.out.println("    - Selector: 选择器（多路复用）");
        System.out.println("");
        System.out.println("  NIO 优势:");
        System.out.println("    - 非阻塞 I/O");
        System.out.println("    - 单线程管理多个连接");
        System.out.println("    - 适合高并发场景");
        System.out.println("");
        System.out.println("  NIO 服务器模板:");
        System.out.println("    Selector selector = Selector.open();");
        System.out.println("    ServerSocketChannel server = ServerSocketChannel.open();");
        System.out.println("    server.bind(new InetSocketAddress(8080));");
        System.out.println("    server.configureBlocking(false);");
        System.out.println("    server.register(selector, SelectionKey.OP_ACCEPT);");
        System.out.println("    while (selector.select() > 0) {");
        System.out.println("        for (SelectionKey key : selector.selectedKeys()) {");
        System.out.println("            // 处理事件");
        System.out.println("        }");
        System.out.println("    }");
        assertTrue(true);
    }
}
