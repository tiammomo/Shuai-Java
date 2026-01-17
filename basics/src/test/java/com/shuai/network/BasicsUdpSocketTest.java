package com.shuai.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UDP Socket 测试类
 */
@DisplayName("UDP Socket 测试")
class BasicsUdpSocketTest {

    @Test
    @DisplayName("UDP 基本概念测试")
    void udpConceptTest() {
        System.out.println("  UDP (User Datagram Protocol) 特点:");
        System.out.println("    - 无连接：不需要建立连接");
        System.out.println("    - 不可靠：不保证送达");
        System.out.println("    - 高效：头部小，开销低");
        System.out.println("    - 支持广播/多播");
        System.out.println("");
        System.out.println("  UDP 应用场景:");
        System.out.println("    - DNS 查询");
        System.out.println("    - 视频流媒体");
        System.out.println("    - 在线游戏");
        System.out.println("    - IoT 设备通信");
        assertTrue(true);
    }

    @Test
    @DisplayName("DatagramSocket 用法")
    void datagramSocketTest() {
        System.out.println("  DatagramSocket 常用方法:");
        System.out.println("    new DatagramSocket()               - 随机端口");
        System.out.println("    new DatagramSocket(port)           - 指定端口");
        System.out.println("    send(DatagramPacket)               - 发送数据报");
        System.out.println("    receive(DatagramPacket)            - 接收数据报（阻塞）");
        System.out.println("    getLocalPort()                     - 获取本地端口");
        System.out.println("    getLocalAddress()                  - 获取本地地址");
        System.out.println("    setSoTimeout(timeout)              - 设置接收超时");
        System.out.println("    close()                            - 关闭套接字");
        assertTrue(true);
    }

    @Test
    @DisplayName("DatagramPacket 用法")
    void datagramPacketTest() {
        System.out.println("  DatagramPacket 常用构造方法:");
        System.out.println("    // 发送数据报");
        System.out.println("    new DatagramPacket(data, length, address, port)");
        System.out.println("    new DatagramPacket(data, offset, length, address, port)");
        System.out.println("");
        System.out.println("    // 接收数据报");
        System.out.println("    new DatagramPacket(data, length)");
        System.out.println("    new DatagramPacket(data, offset, length)");
        System.out.println("");
        System.out.println("  常用方法:");
        System.out.println("    getData()       - 获取数据数组");
        System.out.println("    getLength()     - 获取数据长度");
        System.out.println("    getAddress()    - 获取来源地址");
        System.out.println("    getPort()       - 获取来源端口");
        assertTrue(true);
    }

    @Test
    @DisplayName("UDP 服务器客户端示例")
    void udpServerClientTest() {
        System.out.println("  UDP 服务器模板:");
        System.out.println("    try (DatagramSocket socket = new DatagramSocket(8888)) {");
        System.out.println("        byte[] buffer = new byte[1024];");
        System.out.println("        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);");
        System.out.println("        socket.receive(packet);  // 阻塞等待");
        System.out.println("        String msg = new String(packet.getData(), 0, packet.getLength());");
        System.out.println("        // 处理消息...");
        System.out.println("        // 响应客户端");
        System.out.println("        DatagramPacket response = new DatagramPacket(");
        System.out.println("            responseData, responseData.length, packet.getAddress(), packet.getPort());");
        System.out.println("        socket.send(response);");
        System.out.println("    }");
        System.out.println("");
        System.out.println("  UDP 客户端模板:");
        System.out.println("    try (DatagramSocket socket = new DatagramSocket()) {");
        System.out.println("        String message = \"Hello UDP Server\";");
        System.out.println("        byte[] data = message.getBytes();");
        System.out.println("        InetAddress serverAddr = InetAddress.getByName(\"localhost\");");
        System.out.println("        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, 8888);");
        System.out.println("        socket.send(packet);");
        System.out.println("        // 接收响应");
        System.out.println("        byte[] buffer = new byte[1024];");
        System.out.println("        DatagramPacket response = new DatagramPacket(buffer, buffer.length);");
        System.out.println("        socket.receive(response);");
        System.out.println("    }");
        assertTrue(true);
    }

    @Test
    @DisplayName("UDP 与 TCP 对比")
    void udpVsTcpTest() {
        System.out.println("  UDP vs TCP 对比:");
        System.out.println("    | 特性         | UDP                | TCP               |");
        System.out.println("    |--------------|--------------------|-------------------|");
        System.out.println("    | 连接方式      | 无连接              | 面向连接           |");
        System.out.println("    | 可靠性       | 不可靠              | 可靠              |");
        System.out.println("    | 速度         | 快                  | 慢                |");
        System.out.println("    | 数据边界     | 有                  | 无                |");
        System.out.println("    | 广播/多播    | 支持                | 不支持            |");
        System.out.println("    | 头部大小     | 8 字节              | 20 字节           |");
        System.out.println("");
        System.out.println("  选择建议:");
        System.out.println("    - 需要可靠性：选 TCP");
        System.out.println("    - 需要高性能：选 UDP");
        System.out.println("    - 实时性要求高：选 UDP");
        System.out.println("    - 一对多通信：选 UDP");
        assertTrue(true);
    }

    @Test
    @DisplayName("UDP 广播测试")
    void udpBroadcastTest() {
        System.out.println("  UDP 广播:");
        System.out.println("    - 向局域网内所有设备发送数据");
        System.out.println("    - 广播地址：255.255.255.255");
        System.out.println("    - 或使用子网广播地址：如 192.168.1.255");
        System.out.println("");
        System.out.println("  广播示例:");
        System.out.println("    InetAddress broadcast = InetAddress.getByName(\"255.255.255.255\");");
        System.out.println("    DatagramPacket packet = new DatagramPacket(data, data.length, broadcast, 8888);");
        System.out.println("    socket.setBroadcast(true);  // 需要系统权限");
        System.out.println("    socket.send(packet);");
        System.out.println("");
        System.out.println("  应用场景:");
        System.out.println("    - 服务发现（类似 mDNS）");
        System.out.println("    - 在线游戏玩家发现");
        System.out.println("    - 网络配置协议（如 DHCP）");
        assertTrue(true);
    }

    @Test
    @DisplayName("UDP 丢包问题测试")
    void udpPacketLossTest() {
        System.out.println("  UDP 丢包原因:");
        System.out.println("    - 网络拥塞");
        System.out.println("    - 缓冲区溢出");
        System.out.println("    - 防火墙过滤");
        System.out.println("");
        System.out.println("  减少丢包的措施:");
        System.out.println("    1. 控制发送速率（流量控制）");
        System.out.println("    2. 分包发送（避免过大的包）");
        System.out.println("    3. 增加重传机制（应用层）");
        System.out.println("    4. 使用更可靠的网络");
        System.out.println("");
        System.out.println("  应用层确认机制:");
        System.out.println("    // 发送端：添加序号");
        System.out.println("    packet = new DatagramPacket(data, data.length, addr, port);");
        System.out.println("    seqNum = (seqNum + 1) % 256;");
        System.out.println("    out.writeByte(seqNum);");
        System.out.println("");
        System.out.println("    // 接收端：发送 ACK");
        System.out.println("    if (checkSumValid(data)) {");
        System.out.println("        sendAck(seqNum);");
        System.out.println("    }");
        assertTrue(true);
    }

    @Test
    @DisplayName("UDP NIO 通道测试")
    void udpNioChannelTest() {
        System.out.println("  UDP NIO 组件:");
        System.out.println("    - DatagramChannel: UDP 通道");
        System.out.println("    - Selector: 选择器");
        System.out.println("");
        System.out.println("  UDP NIO 示例:");
        System.out.println("    DatagramChannel channel = DatagramChannel.open();");
        System.out.println("    channel.configureBlocking(false);");
        System.out.println("    channel.bind(new InetSocketAddress(8888));");
        System.out.println("    Selector selector = Selector.open();");
        System.out.println("    channel.register(selector, SelectionKey.OP_READ);");
        System.out.println("");
        System.out.println("    while (selector.select() > 0) {");
        System.out.println("        for (SelectionKey key : selector.selectedKeys()) {");
        System.out.println("            if (key.isReadable()) {");
        System.out.println("                DatagramChannel dc = (DatagramChannel) key.channel();");
        System.out.println("                ByteBuffer buffer = ByteBuffer.allocate(1024);");
        System.out.println("                SocketAddress addr = dc.receive(buffer);");
        System.out.println("                buffer.flip();");
        System.out.println("                // 处理数据...");
        System.out.println("            }");
        System.out.println("        }");
        System.out.println("    }");
        assertTrue(true);
    }
}
