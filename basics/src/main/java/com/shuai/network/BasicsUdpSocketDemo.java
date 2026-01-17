package com.shuai.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP Socket 编程演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsUdpSocketDemo {

    private static final int PORT = 9999;

    public void runAllDemos() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       UDP Socket 编程");
        System.out.println("=".repeat(50));

        udpOverview();
        datagramSocketDemo();
        datagramPacketDemo();
    }

    /**
     * UDP 概述
     */
    private void udpOverview() {
        System.out.println("\n--- UDP 概述 ---");

        System.out.println("\n  UDP 特点:");
        System.out.println("    - 无连接 (Connectionless)");
        System.out.println("    - 不可靠传输 (Unreliable)");
        System.out.println("    - 数据报 (Datagram)");
        System.out.println("    - 尽最大努力交付 (Best-effort)");
        System.out.println("    - 低延迟 (Low latency)");

        System.out.println("\n  适用场景:");
        System.out.println("    - 实时音视频 (RTP/RTSP)");
        System.out.println("    - DNS 查询");
        System.out.println("    - 在线游戏");
        System.out.println("    - 广播/组播");
        System.out.println("    - 简单查询类应用");

        System.out.println("\n  核心类:");
        System.out.println("    DatagramSocket   - UDP 套接字");
        System.out.println("    DatagramPacket   - UDP 数据报");
        System.out.println("    InetAddress      - IP 地址");
    }

    /**
     * DatagramSocket 演示
     */
    private void datagramSocketDemo() {
        System.out.println("\n--- DatagramSocket ---");

        System.out.println("\n  构造方法:");
        System.out.println("    new DatagramSocket()                    - 随机端口");
        System.out.println("    new DatagramSocket(port)                - 指定端口");
        System.out.println("    new DatagramSocket(port, address)       - 绑定地址");
        System.out.println("    new DatagramSocket(SocketAddress)       - 绑定 SocketAddress");

        System.out.println("\n  常用方法:");
        System.out.println("    send(DatagramPacket)    - 发送数据报");
        System.out.println("    receive(DatagramPacket) - 接收数据报 (阻塞)");
        System.out.println("    setSoTimeout(timeout)   - 设置超时");
        System.out.println("    getLocalPort()          - 获取本地端口");
        System.out.println("    close()                 - 关闭");

        System.out.println("\n(");
        System.out.println("    // 发送端");
        System.out.println("    try (DatagramSocket socket = new DatagramSocket()) {");
        System.out.println("        String message = \"Hello UDP\";");
        System.out.println("        DatagramPacket packet = new DatagramPacket(");
        System.out.println("            message.getBytes(), message.length(),");
        System.out.println("            InetAddress.getLocalHost(), PORT);");
        System.out.println("        socket.send(packet);");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 接收端");
        System.out.println("    try (DatagramSocket socket = new DatagramSocket(PORT)) {");
        System.out.println("        byte[] buffer = new byte[1024];");
        System.out.println("        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);");
        System.out.println("        socket.receive(packet);");
        System.out.println("        String message = new String(packet.getData(), 0, packet.getLength());");
        System.out.println("    }");
    }

    /**
     * DatagramPacket 演示
     */
    private void datagramPacketDemo() {
        System.out.println("\n--- DatagramPacket ---");

        System.out.println("\n  构造方法 (发送):");
        System.out.println("    DatagramPacket(byte[] data, int length, InetAddress address, int port)");
        System.out.println("    DatagramPacket(byte[] data, int length, SocketAddress address)");

        System.out.println("\n  构造方法 (接收):");
        System.out.println("    DatagramPacket(byte[] data, int length)");
        System.out.println("    DatagramPacket(byte[] data, int offset, int length)");

        System.out.println("\n(");
        System.out.println("    // 创建发送数据报");
        System.out.println("    String message = \"Hello UDP\";");
        System.out.println("    byte[] data = message.getBytes();");
        System.out.println("    InetAddress address = InetAddress.getByName(\"localhost\");");
        System.out.println("    DatagramPacket sendPacket = new DatagramPacket(");
        System.out.println("        data, data.length, address, 9999);");
        System.out.println("    ");
        System.out.println("    // 创建接收数据报");
        System.out.println("    byte[] receiveBuffer = new byte[1024];");
        System.out.println("    DatagramPacket receivePacket = new DatagramPacket(");
        System.out.println("        receiveBuffer, receiveBuffer.length);");
        System.out.println("    socket.receive(receivePacket);");
        System.out.println("    ");
        System.out.println("    // 获取信息");
        System.out.println("    String received = new String(receivePacket.getData(), 0, receivePacket.getLength());");
        System.out.println("    InetAddress sender = receivePacket.getAddress();");
        System.out.println("    int senderPort = receivePacket.getPort();");
    }
}
