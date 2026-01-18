package com.shuai.caffeine.util;

import com.shuai.caffeine.model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DataLoader 单元测试
 */
class DataLoaderTest {

    @Test
    void testLoadUserData() {
        UserData user = DataLoader.loadUserData("user:1");
        assertNotNull(user);
        assertEquals("user:1", user.getId());
    }

    @Test
    void testLoadUserDataConsistency() {
        // Same key should return same name
        UserData user1 = DataLoader.loadUserData("user:1");
        UserData user2 = DataLoader.loadUserData("user:1");
        assertEquals(user1.getName(), user2.getName());
    }

    @Test
    void testLoadStringData() {
        String data = DataLoader.loadStringData("test-key");
        assertNotNull(data);
        assertTrue(data.startsWith("Loaded-"));
        assertTrue(data.contains("test-key"));
    }

    @Test
    void testGetNameByIndex() {
        assertEquals("Alice", DataLoader.getNameByIndex(0));
        assertEquals("Bob", DataLoader.getNameByIndex(1));
        assertEquals("Charlie", DataLoader.getNameByIndex(2));
        // Wrap around
        assertEquals("Alice", DataLoader.getNameByIndex(5));
    }
}
