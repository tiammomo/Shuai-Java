package com.shuai.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Java IO 流演示类
 *
 * 涵盖内容：
 * - IO 流分类：输入流、输出流、字节流、字符流
 * - 字节流：InputStream、OutputStream 及其子类
 * - 字符流：Reader、Writer 及其子类
 * - 缓冲流：BufferedInputStream、BufferedReader
 * - 数据流：DataInputStream、DataOutputStream
 * - 对象流：ObjectInputStream、ObjectOutputStream（序列化）
 * - NIO：Path、Files、Channels、Buffer
 * - 文件操作：创建、删除、复制、移动
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsIoDemo {

    /**
     * 执行所有 IO 演示
     */
    public void runAllDemos() {
        byteStream();
        characterStream();
        bufferedStream();
        dataStream();
        objectStream();
        nioFiles();
        pathOperations();
        fileOperations();
        walkAndWatch();
        nioChannels();
    }

    /**
     * 演示字节流
     *
     * 基类：
     * - InputStream：字节输入流
     * - OutputStream：字节输出流
     *
     * 常用实现：
     * - FileInputStream / FileOutputStream：文件字节流
     * - ByteArrayInputStream / ByteArrayOutputStream：内存字节流
     * - FilterInputStream / FilterOutputStream：过滤流
     */
    private void byteStream() {
        // FileInputStream：读取文件字节
        try (InputStream in = new FileInputStream("input.txt")) {
            int data;
            while ((data = in.read()) != -1) {
                // 处理每个字节
                System.out.print((char) data);
            }
        } catch (IOException e) {
            // 处理异常
        }

        // FileOutputStream：写入文件字节
        try (OutputStream out = new FileOutputStream("output.txt")) {
            byte[] bytes = "Hello".getBytes();
            out.write(bytes);
        } catch (IOException e) {
            // 处理异常
        }

        // ByteArrayInputStream：内存读取
        byte[] data = "test data".getBytes();
        try (InputStream in = new ByteArrayInputStream(data)) {
            int b;
            StringBuilder sb = new StringBuilder();
            while ((b = in.read()) != -1) {
                sb.append((char) b);
            }
            System.out.println("  读取内容: " + sb);
        } catch (IOException e) {
            // 不会发生
        }

        // ByteArrayOutputStream：写入内存
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            out.write("data".getBytes());
            byte[] result = out.toByteArray();
            System.out.println("  写入字节数: " + result.length);
        } catch (IOException e) {
            // 不会发生
        }
    }

    /**
     * 演示字符流
     *
     * 基类：
     * - Reader：字符输入流
     * - Writer：字符输出流
     *
     * 常用实现：
     * - FileReader / FileWriter：文件字符流
     * - StringReader / StringWriter：字符串字符流
     * - InputStreamReader / OutputStreamWriter：转换流（字节转字符）
     */
    private void characterStream() {
        // FileReader：读取字符
        try (Reader reader = new FileReader("file.txt")) {
            int ch;
            while ((ch = reader.read()) != -1) {
                // 处理每个字符
            }
        } catch (IOException e) {
            // 处理异常
        }

        // FileWriter：写入字符
        try (Writer writer = new FileWriter("output.txt")) {
            writer.write("Hello World");
        } catch (IOException e) {
            // 处理异常
        }

        // InputStreamReader：字节流转字符流（可指定编码）
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("file.txt"), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
        } catch (IOException e) {
            // 处理异常
        }

        // OutputStreamWriter：字符流转字节流
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream("output.txt"), StandardCharsets.UTF_8)) {
            writer.write("中文内容");
        } catch (IOException e) {
            // 处理异常
        }

        // StringReader：读取字符串
        String text = "Hello";
        try (StringReader reader = new StringReader(text)) {
            int ch = reader.read();
        } catch (IOException e) {
            // 不会发生
        }

        // StringWriter：写入字符串
        try (StringWriter writer = new StringWriter()) {
            writer.write("Hello");
            String result = writer.toString();
        } catch (IOException e) {
            // 不会发生
        }
    }

    /**
     * 演示缓冲流
     *
     * 作用：减少 IO 次数，提高效率
     * - BufferedInputStream：缓冲字节输入
     * - BufferedOutputStream：缓冲字节输出
     * - BufferedReader：缓冲字符输入
     * - BufferedWriter：缓冲字符输出
     */
    private void bufferedStream() {
        // BufferedInputStream：带缓冲的字节输入
        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream("file.bin"))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                // 处理数据
            }
        } catch (IOException e) {
            // 处理异常
        }

        // BufferedOutputStream：带缓冲的字节输出
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream("file.bin"))) {
            byte[] data = "test".getBytes();
            bos.write(data);
        } catch (IOException e) {
            // 处理异常
        }

        // BufferedReader：带缓冲的字符输入
        try (BufferedReader reader = new BufferedReader(
                new FileReader("file.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 处理每行
            }
        } catch (IOException e) {
            // 处理异常
        }

        // BufferedWriter：带缓冲的字符输出
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("output.txt"))) {
            writer.write("line1");
            writer.newLine();
            writer.write("line2");
        } catch (IOException e) {
            // 处理异常
        }
    }

    /**
     * 演示数据流
     *
     * 作用：读写基本数据类型
     * - DataInputStream：读取基本类型
     * - DataOutputStream：写入基本类型
     */
    private void dataStream() {
        // DataOutputStream：写入基本类型
        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream("data.bin"))) {
            dos.writeInt(42);
            dos.writeDouble(3.14);
            dos.writeBoolean(true);
            dos.writeUTF("Hello");
        } catch (IOException e) {
            // 处理异常
        }

        // DataInputStream：读取基本类型
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream("data.bin"))) {
            int intVal = dis.readInt();
            double doubleVal = dis.readDouble();
            boolean boolVal = dis.readBoolean();
            String strVal = dis.readUTF();
        } catch (IOException e) {
            // 处理异常
        }
    }

    /**
     * 演示对象流
     *
     * 作用：序列化/反序列化对象
     * - ObjectOutputStream：写入对象（序列化）
     * - ObjectInputStream：读取对象（反序列化）
     *
     * 注意：对象必须实现 Serializable 接口
     */
    private void objectStream() {
        // 序列化对象
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("object.dat"))) {
            Person person = new Person("张三", 25);
            oos.writeObject(person);
        } catch (IOException e) {
            // 处理异常
        }

        // 反序列化对象
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("object.dat"))) {
            Person person = (Person) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // 处理异常
        }

        // serialVersionUID 演示
        // 不指定时，JVM 会根据类结构自动生成
        // 修改类结构后，serialVersionUID 会改变，导致反序列化失败
        // 建议：显式声明 serialVersionUID
    }

    // 可序列化的示例类
    static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    /**
     * 演示 NIO Files 工具类
     *
     * java.nio.file.Files 提供便捷的文件操作
     */
    private void nioFiles() {
        Path path = Paths.get("test.txt");

        // 检查文件是否存在
        boolean exists = Files.exists(path);

        // 创建文件
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            // 文件已存在
        } catch (IOException e) {
            // 处理异常
        }

        // 创建目录
        try {
            Files.createDirectories(Paths.get("dir/subdir"));
        } catch (IOException e) {
            // 处理异常
        }

        // 读取文件所有内容
        try {
            byte[] bytes = Files.readAllBytes(path);
            String content = Files.readString(path);
        } catch (IOException e) {
            // 处理异常
        }

        // 写入文件
        try {
            Files.write(path, "content".getBytes());
            Files.writeString(path, "content");
        } catch (IOException e) {
            // 处理异常
        }

        // 复制文件
        try {
            Files.copy(path, Paths.get("copy.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // 处理异常
        }

        // 移动/重命名文件
        try {
            Files.move(path, Paths.get("new.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // 处理异常
        }

        // 删除文件
        try {
            Files.delete(path);
        } catch (NoSuchFileException e) {
            // 文件不存在
        } catch (IOException e) {
            // 处理异常
        }

        // 遍历目录
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."))) {
            for (Path entry : stream) {
                // 处理每个条目
            }
        } catch (IOException e) {
            // 处理异常
        }

        // 读取行
        try {
            List<String> lines = Files.readAllLines(path);
        } catch (IOException e) {
            // 处理异常
        }

        // 写入多行
        try {
            Files.write(path, Arrays.asList("line1", "line2"));
        } catch (IOException e) {
            // 处理异常
        }
    }

    /**
     * 演示 Path 操作
     */
    private void pathOperations() {
        System.out.println("\n--- Path 操作 ---");

        System.out.println("\n  创建 Path:");
        System.out.println("    Path path1 = Paths.get(\"/home/user/file.txt\");");
        System.out.println("    Path path2 = Paths.get(\"home\", \"user\", \"file.txt\");");

        System.out.println("\n  Path 方法:");
        System.out.println("    path.getFileName()     - 获取文件名");
        System.out.println("    path.getParent()       - 获取父目录");
        System.out.println("    path.getRoot()         - 获取根目录");
        System.out.println("    path.toAbsolutePath()  - 转为绝对路径");
        System.out.println("    path.normalize()       - 规范化路径");
        System.out.println("    path.resolve(other)    - 拼接路径");
        System.out.println("    path.relativize(other) - 计算相对路径");
        System.out.println("    path.getNameCount()    - 名称组件数量");
        System.out.println("    path.subpath(0, 2)     - 子路径");
    }

    /**
     * 演示 Files 工具类 - 文件操作
     */
    private void fileOperations() {
        System.out.println("\n--- Files 文件操作 ---");

        System.out.println("\n  检查方法:");
        System.out.println("    Files.exists(path)              - 存在");
        System.out.println("    Files.isRegularFile(path)       - 普通文件");
        System.out.println("    Files.isDirectory(path)         - 目录");

        System.out.println("\n  创建方法:");
        System.out.println("    Files.createFile(path)          - 创建文件");
        System.out.println("    Files.createDirectories(path)   - 创建多级目录");

        System.out.println("\n  复制/移动:");
        System.out.println("    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)");
        System.out.println("    Files.move(source, target, StandardCopyOption.ATOMIC_MOVE)");

        System.out.println("\n  删除:");
        System.out.println("    Files.delete(path)              - 删除文件/目录");
        System.out.println("    Files.deleteIfExists(path)      - 存在则删除");
    }

    /**
     * 演示 Files 工具类 - 遍历和监控
     */
    private void walkAndWatch() {
        System.out.println("\n--- 遍历文件树 ---");

        System.out.println("\n  基本遍历:");
        System.out.println("    try (Stream<Path> stream = Files.walk(path)) {");
        System.out.println("        stream.forEach(System.out::println);");
        System.out.println("    }");

        System.out.println("\n  限制深度:");
        System.out.println("    try (Stream<Path> stream = Files.walk(path, 3)) { }");

        System.out.println("\n  查找文件:");
        System.out.println("    Files.find(path, depth, (p, attrs) -> attrs.isRegularFile())");

        System.out.println("\n--- WatchService 监控 ---");

        System.out.println("\n  创建 WatchService:");
        System.out.println("    WatchService watchService = FileSystems.getDefault().newWatchService();");

        System.out.println("\n  注册监控:");
        System.out.println("    dir.register(watchService,");
        System.out.println("        StandardWatchEventKinds.ENTRY_CREATE,");
        System.out.println("        StandardWatchEventKinds.ENTRY_MODIFY,");
        System.out.println("        StandardWatchEventKinds.ENTRY_DELETE);");
    }

    /**
     * 演示 NIO Channel 和 Buffer
     *
     * Channel：通道（类似流，但可双向）
     * Buffer：缓冲区（存储数据）
     */
    private void nioChannels() {
        // 使用 Channel 复制文件
        try (
            FileInputStream fis = new FileInputStream("source.txt");
            FileOutputStream fos = new FileOutputStream("target.txt")
        ) {
            FileChannel sourceChannel = fis.getChannel();
            FileChannel targetChannel = fos.getChannel();

            // 从 channel 读入 buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (sourceChannel.read(buffer) != -1) {
                // 切换为读取模式
                buffer.flip();
                // 从 buffer 写入 channel
                targetChannel.write(buffer);
                // 清空 buffer
                buffer.clear();
            }
        } catch (IOException e) {
            // 处理异常
        }

        // 使用 FileChannel 直接传输
        try (FileChannel source = FileChannel.open(Paths.get("source.txt"));
             FileChannel target = FileChannel.open(Paths.get("target.txt"),
                     StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            // 直接传输（零拷贝）
            long position = 0;
            long count = source.size();
            source.transferTo(position, count, target);
        } catch (IOException e) {
            // 处理异常
        }

        // 不同类型的 Buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);        // 字节
        CharBuffer charBuffer = CharBuffer.allocate(1024);        // 字符
        IntBuffer intBuffer = IntBuffer.allocate(256);            // 整型
        DoubleBuffer doubleBuffer = DoubleBuffer.allocate(128);   // 双精度

        // Buffer 操作
        byteBuffer.put((byte) 1);  // 写入
        byteBuffer.flip();         // 切换为读取模式
        byteBuffer.get();          // 读取
        byteBuffer.rewind();       // 重读
        byteBuffer.clear();        // 清空（position=0, limit=capacity）
        byteBuffer.compact();      // 压缩（保留未读数据）
    }
}
