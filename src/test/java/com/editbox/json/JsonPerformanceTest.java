package com.editbox.json;

import org.junit.Test;

/**
 * Tests for Java serialization/deserialization class.
 *
 * @author Aleksandr Uhanov
 * @since 2019-10-12
 */
public class JsonPerformanceTest {

    @Test
    public void jsonToListOfBooleansTest() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000_000; i++) {
            Json.jsonToList("[true,false,true]");
        }
        System.out.println("Duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }
}
