package com.editbox.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.Test;

/**
 * Tests for Java serialization/deserialization class.
 *
 * @author Aleksandr Uhanov
 * @since 2019-10-12
 */
public class JsonFormatPerformanceTest {

    @Test
    public void performanceTheJsonTest() {
        Entity object = JsonTest.createTestJsonEntity();
        int SIZE = 1000_000;
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            Json.format(object);
        }
        System.out.println("Json serialization duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }

    @Test
    public void performanceGsonTest() {
        Entity object = JsonTest.createTestJsonEntity();
        int SIZE = 1000_000;
        long start = System.nanoTime();
        Gson gson = new Gson();
        for (int i = 0; i < SIZE; i++) {
            gson.toJson(object);
        }
        System.out.println("Gson serialization duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }

    @Test
    public void performanceJacksonTest() throws JsonProcessingException {
        Entity object = JsonTest.createTestJsonEntity();
        int SIZE = 1000_000;
        long start = System.nanoTime();
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < SIZE; i++) {
            mapper.writeValueAsString(object);
        }
        System.out.println("Jackson serialization duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }
}
