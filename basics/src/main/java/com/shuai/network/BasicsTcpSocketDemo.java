package com.shuai.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP Socket 编程演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsTcpSocketDemo {

    private static final int PORT = 8888;

    public void runAllDemos() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       TCP Socket 编程");
        System.out.println("=".repeat(50));

        tcpOverview();
        serverSocketDemo();
        clientSocketDemo();
        echoServerDemo();
    }

    /**
     * TCP Socket 概述
     */
    private void tcpOverview() {
        System.out.println("\n--- TCP Socket 概述 ---");

        System.out.println("\n  TCP 特点:");
        System.out.println("    - 面向连接 (Connection-oriented)");
        System.out.println("    - 可靠传输 (Reliable)");
        System.out.println("    - 字节流 (Byte stream)");
        System.out.println("    - 顺序保证 (Ordered)");
        System.out.println("    - 全双工 (Full-duplex)");

        System.out.println("\n  核心类:");
        System.out.println("    ServerSocket  - 服务器端套接字");
        System.out.println("    Socket        - 客户端/服务器通用套接字");
        System.out.println("    InetAddress   - IP 地址封装");

        System.out.println("\n  通信流程:");
        System.out.println("    服务器: ServerSocket -> accept() -> Socket");
        System.out.println("    客户端: Socket -> connect() -> 建立连接");
        System.out.println("    双方: getInputStream()/getOutputStream() -> IO 流通信");
    }

    /**
     * ServerSocket 演示
     */
    private void serverSocketDemo() {
        System.out.println("\n--- ServerSocket ---");

        System.out.println("\n  构造方法:");
        System.out.println("    new ServerSocket(port)                    - 绑定端口");
        System.out.println("    new ServerSocket(port, backlog)           - 设置连接队列");
        System.out.println("    new ServerSocket(port, backlog, address)  - 绑定地址");

        System.out.println("\n  常用方法:");
        System.out.println("    bind(SocketAddress)     - 绑定地址");
        System.out.println("    accept()                - 接受连接 (阻塞)");
        System.out.println("    getLocalPort()          - 获取本地端口");
        System.out.println("    getSoTimeout()          - 获取超时时间");
        System.out.println("    setSoTimeout(timeout)   - 设置超时时间");
        System.out.println("    close()                 - 关闭");

        System.out.println("\n  示例代码:");
        System.out.println("    try (ServerSocket server = new ServerSocket(8080)) {");
        System.out.println("        System.out.println(\"服务器启动，端口: 8080\");");
        System.out.println("        while (true) {");
        System.out.println("            Socket client = server.accept();");
        System.out.println("            System.out.println(\"新连接: \" + client.getInetAddress());");
        System.out.println("            // 处理客户端请求");
        System.out.println("        }");
        System.out.println("    }");
    }

    /**
     * Socket 客户端演示
     */
    private void clientSocketDemo() {
        System.out.println("\n--- Socket 客户端 ---");

        System.out.println("\n  构造方法:");
        System.out.println("    new Socket(host, port)                    - 直接连接");
        System.out.println("    new Socket(address, port)                 - 使用 InetAddress");
        System.out.println("    new Socket(host, port, localAddr, localPort) - 绑定本地地址");
        System.out.println("    new Socket()                              - 未连接状态");

        System.out.println("\n(");
        System.out.println("    try (Socket socket = new Socket(\"localhost\", 8080)) {");
        System.out.println("        // 设置连接超时");
        System.out.println("        socket.connect(new InetSocketAddress(\"localhost\", 8080), 5000);");
        System.out.println("        ");
        System.out.println("        // 获取 IO 流");
        System.out.println("        BufferedReader in = new BufferedReader(");
        System.out.println("            new InputStreamReader(socket.getInputStream()));");
        System.out.println("        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);");
        System.out.println("        ");
        System.out.println("        // 发送请求");
        System.out.println("        out.println(\"GET / HTTP/1.1\");");
        System.out.println("        out.println(\"Host: localhost\");");
        System.out.println("        out.println();");
        System.out.println("        ");
        System.out.println("        // 读取响应");
        System.out.println("        String response;");
        System.out.println("        while ((response = in.readLine()) != null) {");
        System.out.println("            System.out.println(response);");
        System.out.println("        }");
        System.out.println("    }");

        System.out.println("\n  Socket 选项:");
        System.out.println("    SO_TIMEOUT           - 读取超时");
        System.out.println("    SO_REUSEADDR         - 地址复用");
        System.out.println("    SO_KEEPALIVE         - 保持连接");
        System.out.println("    TCP_NODELAY          - 禁用 Nagle 算法");
        System.out.println("    SO_RCVBUF            - 接收缓冲区大小");
        System.out.println("    SO_SNDBUF            - 发送缓冲区大小");
    }

    /**
     * Echo 服务器示例
     */
    private void echoServerDemo() throws Exception {
        System.out.println("\n--- Echo 服务器示例 ---");

        System.out.println("\n  服务器端:");
        System.out.println("    try (ServerSocket server = new ServerSocket(PORT)) {");
        System.out.println("        System.out.println(\"服务器启动，端口: \" + PORT);");
        System.out.println("        ");
        System.out.println("        try (Socket client = server.accept()) {");
        System.out.println("            BufferedReader in = new BufferedReader(");
        System.out.println("                new InputStreamReader(client.getInputStream()));");
        System.out.println("            PrintWriter out = new PrintWriter(client.getOutputStream(), true);");
        System.out.println("            ");
        System.out.println("            String message;");
        System.out.println("            while ((message = in.readLine()) != null) {");
        System.out.println("                System.out.println(\"收到: \" + message);");
        System.out.println("                out.println(\"Echo: \" + message.toUpperCase());");
        System.out.println("            }");
        System.out.println("        }");
        System.out.println("    }");

        System.out.println("\n  客户端连接:");
        System.out.println("    try (Socket socket = new Socket(\"localhost\", PORT);");
        System.out.println("         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);");
        System.out.println("         BufferedReader in = new BufferedReader(");
        System.out.println("             new InputStreamReader(socket.getInputStream()))) {");
        System.out.println("        ");
        System.out.println("        out.println(\"Hello TCP Server\");");
        System.out.println("        System.out.println(\"收到: \" + in.readLine());  // Echo: HELLO TCP SERVER");
        System.out.println("    }");
    }
}
