package com.shuai.netty;

/**
 * Netty 网络编程演示
 *
 * @author Shuai
 * @version 1.0
 */

/**
 * Netty 网络编程演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsNettyDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Netty 高性能网络编程");
        System.out.println("=".repeat(50));

        nettyOverview();
        byteBufDemo();
        channelDemo();
        pipelineDemo();
        codecDemo();
    }

    /**
     * Netty 概述
     */
    private void nettyOverview() {
        System.out.println("\n--- Netty 概述 ---");

        System.out.println("\n  Netty 特点:");
        System.out.println("    - 高性能: 基于 NIO 的异步事件驱动");
        System.out.println("    - 高并发: 支持百万级连接");
        System.out.println("    - 易用性: 简化 NIO 编程");
        System.out.println("    - 安全性: 支持 SSL/TLS");
        System.out.println("    - 稳定性: 经过大规模生产验证");

        System.out.println("\n  核心组件:");
        System.out.println("    - Channel     - Socket 连接");
        System.out.println("    - ByteBuf     - 字节缓冲区");
        System.out.println("    - ChannelPipeline - 处理链");
        System.out.println("    - ChannelHandler - 处理逻辑");
        System.out.println("    - EventLoop   - 事件循环");
        System.out.println("    - Bootstrap   - 启动配置");

        System.out.println("\n  Maven 依赖:");
        System.out.println("    <dependency>");
        System.out.println("        <groupId>io.netty</groupId>");
        System.out.println("        <artifactId>netty-all</artifactId>");
        System.out.println("        <version>4.1.108.Final</version>");
        System.out.println("    </dependency>");
    }

    /**
     * ByteBuf 演示
     */
    private void byteBufDemo() {
        System.out.println("\n--- ByteBuf ---");

        System.out.println("\n  创建 ByteBuf:");
        System.out.println("    ByteBuf buffer = Unpooled.buffer(1024);");
        System.out.println("    ByteBuf heapBuffer = Unpooled.copiedBuffer(\"hello\".getBytes());");
        System.out.println("    ByteBuf directBuffer = Unpooled.directBuffer(512);");

        System.out.println("\n(");
        System.out.println("    // 堆内存 Buffer (HeapByteBuf)");
        System.out.println("    byte[] heap = new byte[1024];");
        System.out.println("    ByteBuf heapBuf = Unpooled.copiedBuffer(heap);");
        System.out.println("    ");
        System.out.println("    // 直接内存 Buffer (DirectByteBuf)");
        System.out.println("    ByteBuf directBuf = Unpooled.directBuffer(512);");
        System.out.println("    ");
        System.out.println("    // 复合 Buffer (CompositeByteBuf)");
        System.out.println("    CompositeByteBuf composite = Unpooled.compositeBuffer();");

        System.out.println("\n  ByteBuf 操作:");
        System.out.println("    buffer.readBytes(byte[])           - 读取");
        System.out.println("    buffer.writeBytes(byte[])          - 写入");
        System.out.println("    buffer.getByte(int)                - 随机读");
        System.out.println("    buffer.setByte(int, int)           - 随机写");
        System.out.println("    buffer.readableBytes()             - 可读字节数");
        System.out.println("    buffer.readableBytes()             - 可写字节数");
        System.out.println("    buffer.capacity()                  - 容量");

        System.out.println("\n(");
        System.out.println("    // 读写示例");
        System.out.println("    ByteBuf buf = Unpooled.copiedBuffer(\"Netty\".getBytes());");
        System.out.println("    while (buf.isReadable()) {");
        System.out.println("        System.out.print((char) buf.readByte());");
        System.out.println("    }");
    }

    /**
     * Channel 演示
     */
    private void channelDemo() {
        System.out.println("\n--- Channel ---");

        System.out.println("\n  Channel 类型:");
        System.out.println("    NioSocketChannel    - TCP 客户端");
        System.out.println("    NioServerSocketChannel - TCP 服务端");
        System.out.println("    NioDatagramChannel  - UDP");
        System.out.println("    EpollSocketChannel  - Linux epoll");
        System.out.println("    KQueueSocketChannel - macOS kqueue");

        System.out.println("\n  Channel 常用方法:");
        System.out.println("    channel.write(Object)      - 写数据");
        System.out.println("    channel.read(Object)       - 读数据");
        System.out.println("    channel.flush()            - 刷新");
        System.out.println("    channel.close()            - 关闭");
        System.out.println("    channel.isActive()         - 是否活跃");
        System.out.println("    channel.localAddress()     - 本地地址");
        System.out.println("    channel.remoteAddress()    - 远程地址");

        System.out.println("\n  连接示例:");
        System.out.println("    EventLoopGroup group = new NioEventLoopGroup();");
        System.out.println("    Bootstrap bootstrap = new Bootstrap();");
        System.out.println("    Channel channel = bootstrap.group(group)");
        System.out.println("        .channel(NioSocketChannel.class)");
        System.out.println("        .handler(new ChannelInitializer<NioSocketChannel>() {");
        System.out.println("            protected void initChannel(NioSocketChannel ch) {");
        System.out.println("                ch.pipeline().addLast(new ClientHandler());");
        System.out.println("            }");
        System.out.println("        })");
        System.out.println("        .connect(\"localhost\", 8080)");
        System.out.println("        .sync()");
        System.out.println("        .channel();");
    }

    /**
     * Pipeline 演示
     */
    private void pipelineDemo() {
        System.out.println("\n--- ChannelPipeline ---");

        System.out.println("\n  Pipeline 结构:");
        System.out.println("    ChannelPipeline 由多个 ChannelHandler 组成");
        System.out.println("   Inbound  <--->  Outbound");
        System.out.println("    [head] -> H1 -> H2 -> H3 -> [tail]");

        System.out.println("\n  ChannelHandler 类型:");
        System.out.println("    ChannelInboundHandler  - 处理入站事件");
        System.out.println("    ChannelOutboundHandler - 处理出站事件");
        System.out.println("    ChannelHandlerAdapter  - 适配器");

        System.out.println("\n  常用入站事件:");
        System.out.println("    channelRegistered     - 注册到 EventLoop");
        System.out.println("    channelActive         - 连接激活");
        System.out.println("    channelRead           - 读取数据");
        System.out.println("    channelReadComplete   - 读取完成");
        System.out.println("    channelInactive       - 连接关闭");
        System.out.println("    exceptionCaught       - 异常");

        System.out.println("\n  常用出站事件:");
        System.out.println("    bind                  - 绑定地址");
        System.out.println("    connect               - 连接");
        System.out.println("    write                 - 写数据");
        System.out.println("    flush                 - 刷新");
        System.out.println("    disconnect            - 断开连接");
        System.out.println("    close                 - 关闭");

        System.out.println("\n(");
        System.out.println("    public class MyHandler extends ChannelInboundHandlerAdapter {");
        System.out.println("        @Override");
        System.out.println("        public void channelRead(ChannelHandlerContext ctx, Object msg) {");
        System.out.println("            // 处理读取的数据");
        System.out.println("            ctx.writeAndFlush(msg);  // 响应");
        System.out.println("        }");
        System.out.println("        ");
        System.out.println("        @Override");
        System.out.println("        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {");
        System.out.println("            cause.printStackTrace();");
        System.out.println("            ctx.close();");
        System.out.println("        }");
        System.out.println("    }");
    }

    /**
     * 编解码器演示
     */
    private void codecDemo() {
        System.out.println("\n--- 编解码器 ---");

        System.out.println("\n(");
        System.out.println("    // LineBasedFrameDecoder (按行分割)");
        System.out.println("    pipeline.addLast(new LineBasedFrameDecoder(1024));");
        System.out.println("    ");
        System.out.println("    // DelimiterBasedFrameDecoder (按分隔符)");
        System.out.println("    ByteBuf delimiter = Unpooled.copiedBuffer(\"$\", StandardCharsets.UTF_8);");
        System.out.println("    pipeline.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));");
        System.out.println("    ");
        System.out.println("    // FixedLengthFrameDecoder (固定长度)");
        System.out.println("    pipeline.addLast(new FixedLengthFrameDecoder(64));");
        System.out.println("    ");
        System.out.println("    // LengthFieldPrepender (长度字段前置)");
        System.out.println("    pipeline.addLast(new LengthFieldPrepender(4));");
        System.out.println("    ");
        System.out.println("    // LengthFieldBasedFrameDecoder (长度字段解码)");
        System.out.println("    pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4));");

        System.out.println("\n  String 编码解码:");
        System.out.println("    pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));");
        System.out.println("    pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));");

        System.out.println("\n  Object 编码解码:");
        System.out.println("    pipeline.addLast(new ObjectEncoder());");
        System.out.println("    pipeline.addLast(new ObjectDecoder(65536));");

        System.out.println("\n  自定义编解码:");
        System.out.println("    public class MyEncoder extends MessageToByteEncoder<MyMessage> {");
        System.out.println("        protected void encode(ChannelHandlerContext ctx, MyMessage msg, ByteBuf out) {");
        System.out.println("            // 编码逻辑");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    public class MyDecoder extends ByteToMessageDecoder {");
        System.out.println("        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {");
        System.out.println("            // 解码逻辑");
        System.out.println("        }");
        System.out.println("    }");
    }
}
