package com.shuai.spi;

public interface SerializationService {
    String getName();
    byte[] serialize(String data);
    String deserialize(byte[] data);
}
