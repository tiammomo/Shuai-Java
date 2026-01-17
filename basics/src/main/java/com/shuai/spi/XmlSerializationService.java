package com.shuai.spi;

public class XmlSerializationService implements SerializationService {
    @Override
    public String getName() {
        return "XML";
    }

    @Override
    public byte[] serialize(String data) {
        return ("<data>" + data + "</data>").getBytes();
    }

    @Override
    public String deserialize(byte[] data) {
        String xml = new String(data);
        int start = xml.indexOf(">") + 1;  // 找到第一个 > (开标签结束)
        int end = xml.lastIndexOf("<");    // 找到最后一个 < (闭标签开始)
        return xml.substring(start, end);
    }
}
