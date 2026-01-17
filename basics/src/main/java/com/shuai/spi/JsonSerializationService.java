package com.shuai.spi;

public class JsonSerializationService implements SerializationService {
    @Override
    public String getName() {
        return "JSON";
    }

    @Override
    public byte[] serialize(String data) {
        return ("{\"data\":\"" + data + "\"}").getBytes();
    }

    @Override
    public String deserialize(byte[] data) {
        String json = new String(data);
        int start = json.indexOf(":\"") + 2;
        int end = json.indexOf("\"}");
        return json.substring(start, end);
    }
}
