package com.shuai.network;

/**
 * Java 网络编程演示类
 *
 * 涵盖内容：
 * - URL 和 URLConnection：资源定位和连接
 * - HttpURLConnection：HTTP 请求
 * - Socket：TCP 套接字编程
 * - ServerSocket：服务器套接字
 * - UDP 编程：DatagramSocket
 * - 域名解析：InetAddress
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsNetworkDemo {

    /**
     * 执行所有网络编程演示
     */
    public void runAllDemos() {
        inetAddressDemo();
        urlDemo();
        httpUrlConnectionDemo();
        socketDemo();
        serverSocketDemo();
        udpDemo();
    }

    /**
     * 演示 InetAddress（IP 地址）
     *
     * InetAddress 表示 IP 地址
     * 用于域名解析和 IP 操作
     */
    private void inetAddressDemo() {
        System.out.println("\n--- InetAddress ---");

        System.out.println("\n  获取本机地址:");
        System.out.println("    InetAddress localHost = InetAddress.getLocalHost();");
        System.out.println("    String hostName = localHost.getHostName();  // 计算机名");
        System.out.println("    String hostAddress = localHost.getHostAddress();  // IP 地址");

        System.out.println("\n  通过域名获取地址:");
        System.out.println("    InetAddress google = InetAddress.getByName(\"www.google.com\");");
        System.out.println("    String ip = google.getHostAddress();");

        System.out.println("\n  获取所有地址（一个域名可能对应多个 IP）:");
        System.out.println("    InetAddress[] addresses = InetAddress.getAllByName(\"www.baidu.com\");");
        System.out.println("    for (InetAddress addr : addresses) {");
        System.out.println("        System.out.println(addr.getHostAddress());");
        System.out.println("    }");

        System.out.println("\n  判断是否可达:");
        System.out.println("    boolean reachable = InetAddress.getByName(\"127.0.0.1\").isReachable(1000);");

        System.out.println("\n  InetAddress 方法:");
        System.out.println("    getHostName()       - 获取主机名");
        System.out.println("    getHostAddress()    - 获取 IP 地址");
        System.out.println("    getCanonicalHostName() - 获取规范主机名");
        System.out.println("    isReachable(timeout)- 测试是否可达");
        System.out.println("    isLoopbackAddress() - 是否回环地址");
    }

    /**
     * 演示 URL（统一资源定位符）
     *
     * URL 表示网络资源的地址
     * 用于定位和访问网络资源
     */
    private void urlDemo() {
        System.out.println("\n--- URL ---");

        System.out.println("\n  URL 组成:");
        System.out.println("    协议://主机名:端口/路径?查询参数#片段");
        System.out.println("    示例: https://www.example.com:443/search?q=java#results");

        System.out.println("\n  创建 URL:");
        System.out.println("    URL url = new URL(\"https://www.example.com/path?query=value\");");

        System.out.println("\n  解析 URL 组件:");
        System.out.println("    String protocol = url.getProtocol();    // https");
        System.out.println("    String host = url.getHost();            // www.example.com");
        System.out.println("    int port = url.getPort();               // 443（默认端口返回 -1）");
        System.out.println("    String path = url.getPath();            // /path");
        System.out.println("    String query = url.getQuery();          // query=value");
        System.out.println("    String ref = url.getRef();              // results（片段）");

        System.out.println("\n  URLEncoder/URLDecoder（URL 编码）:");
        System.out.println("    // 将字符串编码为 URL 安全格式");
        System.out.println("    String encoded = URLEncoder.encode(\"Java 编程\", StandardCharsets.UTF_8);");
        System.out.println("    // 输出: Java+%E7%BC%96%E7%A8%8B");
        System.out.println("");
        System.out.println("    // 解码");
        System.out.println("    String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);");

        System.out.println("\n  读取 URL 资源:");
        System.out.println("    URL url = new URL(\"https://www.example.com/data.txt\");");
        System.out.println("    try (InputStream is = url.openStream()) {");
        System.out.println("        // 读取内容");
        System.out.println("    }");

        System.out.println("\n  使用 URLConnection:");
        System.out.println("    URLConnection conn = url.openConnection();");
        System.out.println("    conn.setConnectTimeout(5000);  // 连接超时");
        System.out.println("    conn.setReadTimeout(10000);    // 读取超时");
        System.out.println("    try (InputStream is = conn.getInputStream()) {");
        System.out.println("        // 读取内容");
        System.out.println("    }");
    }

    /**
     * 演示 HttpURLConnection（HTTP 请求）
     *
     * HttpURLConnection 是 Java 标准库中发送 HTTP 请求的方式
     * 支持 GET、POST 等 HTTP 方法
     */
    private void httpUrlConnectionDemo() {
        System.out.println("\n--- HttpURLConnection ---");

        System.out.println("\n  HTTP 方法:");
        System.out.println("    GET     - 获取资源（默认）");
        System.out.println("    POST    - 提交数据");
        System.out.println("    PUT     - 更新资源");
        System.out.println("    DELETE  - 删除资源");
        System.out.println("    HEAD    - 获取头部信息");

        System.out.println("\n  发送 GET 请求:");
        System.out.println("    URL url = new URL(\"https://api.example.com/users?id=1\");");
        System.out.println("    HttpURLConnection conn = (HttpURLConnection) url.openConnection();");
        System.out.println("    conn.setRequestMethod(\"GET\");");
        System.out.println("    conn.setConnectTimeout(5000);");
        System.out.println("    conn.setReadTimeout(10000);");
        System.out.println("");
        System.out.println("    int responseCode = conn.getResponseCode();");
        System.out.println("    System.out.println(\"状态码: \" + responseCode);");
        System.out.println("");
        System.out.println("    try (InputStream is = conn.getInputStream()) {");
        System.out.println("        // 读取响应");
        System.out.println("    }");
        System.out.println("    conn.disconnect();");

        System.out.println("\n  发送 POST 请求:");
        System.out.println("    URL url = new URL(\"https://api.example.com/users\");");
        System.out.println("    HttpURLConnection conn = (HttpURLConnection) url.openConnection();");
        System.out.println("    conn.setRequestMethod(\"POST\");");
        System.out.println("    conn.setDoOutput(true);  // 允许输出（用于发送数据）");
        System.out.println("    conn.setRequestProperty(\"Content-Type\", \"application/json\");");
        System.out.println("");
        System.out.println("    // 写入请求体");
        System.out.println("    String jsonBody = \"{\\\"name\\\":\\\"张三\\\",\\\"age\\\":25}\";");
        System.out.println("    try (OutputStream os = conn.getOutputStream()) {");
        System.out.println("        byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);");
        System.out.println("        os.write(input, 0, input.length);");
        System.out.println("    }");
        System.out.println("");
        System.out.println("    // 读取响应");
        System.out.println("    int code = conn.getResponseCode();");
        System.out.println("    try (InputStream is = conn.getInputStream()) {");
        System.out.println("        // 处理响应");
        System.out.println("    }");

        System.out.println("\n  设置请求头:");
        System.out.println("    conn.setRequestProperty(\"Authorization\", \"Bearer token123\");");
        System.out.println("    conn.setRequestProperty(\"Accept\", \"application/json\");");
        System.out.println("    conn.setRequestProperty(\"User-Agent\", \"Java HTTP Client\");");

        System.out.println("\n  读取响应头:");
        System.out.println("    String contentType = conn.getContentType();");
        System.out.println("    int contentLength = conn.getContentLength();");
        System.out.println("    Map<String, List<String>> headers = conn.getHeaderFields();");
        System.out.println("    String location = conn.getHeaderField(\"Location\");  // 重定向地址");

        System.out.println("\n  状态码处理:");
        System.out.println("    2xx 成功: 200 OK, 201 Created");
        System.out.println("    3xx 重定向: 301 Moved Permanently, 302 Found");
        System.out.println("    4xx 客户端错误: 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found");
        System.out.println("    5xx 服务器错误: 500 Internal Server Error, 502 Bad Gateway, 503 Service Unavailable");
    }

    /**
     * 演示 Socket（TCP 客户端）
     *
     * Socket 用于建立 TCP 连接
     * 客户端通过 Socket 连接服务器
     */
    private void socketDemo() {
        System.out.println("\n--- Socket（TCP 客户端）---");

        System.out.println("\n  Socket 构造方法:");
        System.out.println("    new Socket(\"host\", port)           - 阻塞式连接");
        System.out.println("    new Socket(\"host\", port, localAddr, localPort) - 绑定本地地址");
        System.out.println("    new Socket()                        - 创建未连接 Socket");

        System.out.println("\n  连接超时:");
        System.out.println("    Socket socket = new Socket();");
        System.out.println("    socket.connect(new InetSocketAddress(\"host\", port), 5000);");

        System.out.println("\n  发送和接收数据:");
        System.out.println("    try (Socket socket = new Socket(\"localhost\", 8080)) {");
        System.out.println("        // 获取输出流（发送到服务器）");
        System.out.println("        OutputStream os = socket.getOutputStream();");
        System.out.println("        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));");
        System.out.println("        writer.println(\"Hello Server!\");");
        System.out.println("        writer.flush();");
        System.out.println("");
        System.out.println("        // 获取输入流（接收服务器响应）");
        System.out.println("        InputStream is = socket.getInputStream();");
        System.out.println("        BufferedReader reader = new BufferedReader(new InputStreamReader(is));");
        System.out.println("        String response = reader.readLine();");
        System.out.println("        System.out.println(\"Server: \" + response);");
        System.out.println("    }");

        System.out.println("\n  Socket 选项:");
        System.out.println("    socket.setSoTimeout(10000);      // 读取超时");
        System.out.println("    socket.setKeepAlive(true);       // 保持连接");
        System.out.println("    socket.setTcpNoDelay(true);      // 禁用 Nagle 算法（低延迟）");
        System.out.println("    socket.setSendBufferSize(8192);  // 发送缓冲区大小");
        System.out.println("    socket.setReceiveBufferSize(8192); // 接收缓冲区大小");
        System.out.println("    socket.setSoLinger(true, 10);    // 关闭时等待数据发送完成");

        System.out.println("\n  获取 Socket 信息:");
        System.out.println("    InetAddress localAddr = socket.getLocalAddress();");
        System.out.println("    int localPort = socket.getLocalPort();");
        System.out.println("    InetAddress remoteAddr = socket.getInetAddress();");
        System.out.println("    int remotePort = socket.getPort();");
        System.out.println("    boolean connected = socket.isConnected();");
    }

    /**
     * 演示 ServerSocket（TCP 服务器）
     *
     * ServerSocket 用于监听端口
     * 接受客户端连接请求
     */
    private void serverSocketDemo() {
        System.out.println("\n--- ServerSocket（TCP 服务器）---");

        System.out.println("\n  ServerSocket 构造方法:");
        System.out.println("    new ServerSocket(port)                    - 监听指定端口");
        System.out.println("    new ServerSocket(port, backlog)           - 设置连接队列长度");
        System.out.println("    new ServerSocket(port, backlog, addr)     - 绑定指定地址");

        System.out.println("\n  监听端口:");
        System.out.println("    try (ServerSocket server = new ServerSocket(8080)) {");
        System.out.println("        System.out.println(\"服务器启动，监听端口 8080...\");");
        System.out.println("");
        System.out.println("        // 接受客户端连接（阻塞）");
        System.out.println("        Socket clientSocket = server.accept();");
        System.out.println("        System.out.println(\"客户端已连接: \" + clientSocket.getRemoteSocketAddress());");
        System.out.println("");
        System.out.println("        // 处理客户端请求（通常在新线程中）");
        System.out.println("        handleClient(clientSocket);");
        System.out.println("    }");

        System.out.println("\n  非阻塞（使用线程池）:");
        System.out.println("    ExecutorService pool = Executors.newFixedThreadPool(10);");
        System.out.println("    try (ServerSocket server = new ServerSocket(8080)) {");
        System.out.println("        while (true) {");
        System.out.println("            Socket client = server.accept();");
        System.out.println("            pool.execute(() -> handleClient(client));");
        System.out.println("        }");
        System.out.println("    }");

        System.out.println("\n  ServerSocket 选项:");
        System.out.println("    server.setSoTimeout(10000);       // accept() 超时");
        System.out.println("    server.setReuseAddress(true);     // 地址复用");
        System.out.println("    server.setReceiveBufferSize(8192); // 接收缓冲区");

        System.out.println("\n  获取服务器信息:");
        System.out.println("    InetAddress address = server.getInetAddress();");
        System.out.println("    int port = server.getLocalPort();");
    }

    /**
     * 演示 UDP 编程
     *
     * UDP 是无连接协议
     * 使用 DatagramSocket 发送和接收数据报
     */
    private void udpDemo() {
        System.out.println("\n--- UDP 编程 ---");

        System.out.println("\n  UDP 特点:");
        System.out.println("    - 无连接，速度快");
        System.out.println("    - 不保证可靠性");
        System.out.println("    - 数据包有大小限制（通常 < 64KB）");
        System.out.println("    - 适合实时应用（视频、游戏）");

        System.out.println("\n  DatagramSocket（发送端）:");
        System.out.println("    try (DatagramSocket socket = new DatagramSocket()) {");
        System.out.println("        String message = \"Hello UDP!\";");
        System.out.println("        byte[] data = message.getBytes(StandardCharsets.UTF_8);");
        System.out.println("");
        System.out.println("        InetAddress address = InetAddress.getByName(\"localhost\");");
        System.out.println("        DatagramPacket packet = new DatagramPacket(data, data.length, address, 8888);");
        System.out.println("");
        System.out.println("        socket.send(packet);");
        System.out.println("        System.out.println(\"数据已发送\");");
        System.out.println("    }");

        System.out.println("\n  DatagramSocket（接收端）:");
        System.out.println("    try (DatagramSocket socket = new DatagramSocket(8888)) {");
        System.out.println("        byte[] buffer = new byte[1024];");
        System.out.println("        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);");
        System.out.println("");
        System.out.println("        System.out.println(\"等待数据...\");");
        System.out.println("        socket.receive(packet);  // 阻塞");
        System.out.println("");
        System.out.println("        String received = new String(packet.getData(), 0, packet.getLength());");
        System.out.println("        System.out.println(\"收到: \" + received);");
        System.out.println("        System.out.println(\"来源: \" + packet.getSocketAddress());");
        System.out.println("    }");

        System.out.println("\n  DatagramPacket 方法:");
        System.out.println("    getData()           - 获取数据字节数组");
        System.out.println("    getLength()         - 获取数据长度");
        System.out.println("    getAddress()        - 获取来源地址");
        System.out.println("    getPort()           - 获取来源端口");
        System.out.println("    setData(byte[])     - 设置数据");
        System.out.println("    setAddress(InetAddress) - 设置目标地址");
        System.out.println("    setPort(int)        - 设置目标端口");

        System.out.println("\n  MulticastSocket（多播）:");
        System.out.println("    // 用于向多播组发送数据");
        System.out.println("    MulticastSocket ms = new MulticastSocket();");
        System.out.println("    ms.joinGroup(InetAddress.getByName(\"230.0.0.1\"));");
    }
}
