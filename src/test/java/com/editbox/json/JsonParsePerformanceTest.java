package com.editbox.json;

import com.editbox.json.entity.Person;
import com.google.gson.Gson;
import org.junit.Test;

/**
 * Tests for Java serialization/deserialization class.
 *
 * @author Aleksandr Uhanov
 * @since 2019-10-11
 */
public class JsonParsePerformanceTest {

    private static String json = "{\"name\": \"Elon Musk\",\"age\":48, \"single\": true, \"balance\": 800.500," +
            " \"phoneNumbers\": [{\"type\": \"mobile\", \"number\": \"+79005004242\"}, {\"type\": \"home\", " +
            "\"number\": \"+79508888300\"}]}";

    private static int SIZE = 1000_000;

    @Test
    public void performanceTheJsonTest() {
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            Json.parse(json, Person.class);
        }
        System.out.println("Parse duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }

    @Test
    public void performanceGsonTest() {
        long start = System.nanoTime();
        Gson gson = new Gson();
        for (int i = 0; i < SIZE; i++) {
            gson.fromJson(json, Person.class);
        }
        System.out.println("Parse duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }
}
