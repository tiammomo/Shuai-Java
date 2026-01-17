package com.shuai.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 网络编程测试类
 */
@DisplayName("网络编程测试")
class BasicsNetworkTest {

    @Test
    @DisplayName("TCP/IP 模型测试")
    void tcpIpModelTest() {
        System.out.println("  TCP/IP 四层模型:");
        System.out.println("    1. 应用层: HTTP, FTP, SMTP, DNS");
        System.out.println("    2. 传输层: TCP, UDP");
        System.out.println("    3. 网络层: IP, ICMP, ARP");
        System.out.println("    4. 链路层: Ethernet, Wi-Fi");
        System.out.println("");
        System.out.println("  OSI 七层模型:");
        System.out.println("    应用层、表示层、会话层、传输层、网络层、数据链路层、物理层");
        assertTrue(true);
    }

    @Test
    @DisplayName("TCP 协议测试")
    void tcpProtocolTest() {
        System.out.println("  TCP (Transmission Control Protocol) 特点:");
        System.out.println("    - 面向连接: 三次握手建立连接");
        System.out.println("    - 可靠传输: 确认、重传、排序");
        System.out.println("    - 流量控制: 滑动窗口");
        System.out.println("    - 拥塞控制: 慢启动、拥塞避免");
        System.out.println("");
        System.out.println("  TCP 三次握手:");
        System.out.println("    1. 客户端 -> SYN -> 服务器");
        System.out.println("    2. 服务器 -> SYN+ACK -> 客户端");
        System.out.println("    3. 客户端 -> ACK -> 服务器");
        System.out.println("");
        System.out.println("  TCP 四次挥手:");
        System.out.println("    1. 客户端 -> FIN -> 服务器");
        System.out.println("    2. 服务器 -> ACK -> 客户端");
        System.out.println("    3. 服务器 -> FIN -> 客户端");
        System.out.println("    4. 客户端 -> ACK -> 服务器");
        assertTrue(true);
    }

    @Test
    @DisplayName("UDP 协议测试")
    void udpProtocolTest() {
        System.out.println("  UDP (User Datagram Protocol) 特点:");
        System.out.println("    - 无连接: 不需要建立连接");
        System.out.println("    - 不可靠: 不保证送达");
        System.out.println("    - 高效: 头部小，开销低");
        System.out.println("    - 支持广播");
        System.out.println("");
        System.out.println("  UDP 应用场景:");
        System.out.println("    - DNS 查询");
        System.out.println("    - 视频流媒体");
        System.out.println("    - 在线游戏");
        System.out.println("    - IoT 设备通信");
        assertTrue(true);
    }

    @Test
    @DisplayName("HTTP 协议测试")
    void httpProtocolTest() {
        System.out.println("  HTTP (HyperText Transfer Protocol) 特点:");
        System.out.println("    - 基于请求/响应模型");
        System.out.println("    - 无状态协议");
        System.out.println("    - 可扩展性强");
        System.out.println("");
        System.out.println("  HTTP 请求方法:");
        System.out.println("    GET: 获取资源");
        System.out.println("    POST: 创建资源");
        System.out.println("    PUT: 更新资源");
        System.out.println("    DELETE: 删除资源");
        System.out.println("    PATCH: 部分更新");
        System.out.println("");
        System.out.println("  HTTP 状态码:");
        System.out.println("    1xx: 信息性");
        System.out.println("    2xx: 成功 (200 OK, 201 Created)");
        System.out.println("    3xx: 重定向 (301 Moved, 302 Found)");
        System.out.println("    4xx: 客户端错误 (400 Bad Request, 404 Not Found)");
        System.out.println("    5xx: 服务器错误 (500 Internal Error, 503 Service Unavailable)");
        assertTrue(true);
    }

    @Test
    @DisplayName("Socket 编程测试")
    void socketProgrammingTest() {
        System.out.println("  Socket 编程步骤:");
        System.out.println("");
        System.out.println("  服务器端:");
        System.out.println("    1. 创建 ServerSocket: new ServerSocket(port)");
        System.out.println("    2. 等待连接: serverSocket.accept()");
        System.out.println("    3. 获取输入/输出流");
        System.out.println("    4. 数据交换");
        System.out.println("    5. 关闭资源");
        System.out.println("");
        System.out.println("  客户端:");
        System.out.println("    1. 创建 Socket: new Socket(host, port)");
        System.out.println("    2. 获取输入/输出流");
        System.out.println("    3. 数据交换");
        System.out.println("    4. 关闭资源");
        assertTrue(true);
    }

    @Test
    @DisplayName("NIO Socket 测试")
    void nioSocketTest() {
        System.out.println("  Java NIO Socket 组件:");
        System.out.println("    - Channel: 通道 (SocketChannel, ServerSocketChannel)");
        System.out.println("    - Buffer: 缓冲区 (ByteBuffer, CharBuffer)");
        System.out.println("    - Selector: 选择器 (多路复用)");
        System.out.println("");
        System.out.println("  NIO 优势:");
        System.out.println("    - 非阻塞 I/O");
        System.out.println("    - 单线程管理多个连接");
        System.out.println("    - 减少线程上下文切换开销");
        System.out.println("");
        System.out.println("  使用场景:");
        System.out.println("    - 高并发服务器");
        System.out.println("    - 聊天服务器");
        System.out.println("    - 即时通讯系统");
        assertTrue(true);
    }

    @Test
    @DisplayName("URL 和 HttpURLConnection 测试")
    void urlConnectionTest() {
        System.out.println("  URL 组成:");
        System.out.println("    protocol://host:port/path?query#fragment");
        System.out.println("    例如: https://www.example.com:443/path?id=1#section");
        System.out.println("");
        System.out.println("  HttpURLConnection 使用步骤:");
        System.out.println("    1. 创建 URL 对象");
        System.out.println("    2. 打开连接: url.openConnection()");
        System.out.println("    3. 设置请求参数");
        System.out.println("    4. 获取响应码");
        System.out.println("    5. 读取响应内容");
        System.out.println("");
        System.out.println("  请求头设置:");
        System.out.println("    connection.setRequestProperty(\"Content-Type\", \"application/json\")");
        System.out.println("    connection.setRequestProperty(\"Authorization\", \"Bearer token\")");
        assertTrue(true);
    }
}
