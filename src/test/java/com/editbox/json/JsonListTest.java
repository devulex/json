package com.editbox.json;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;

/**
 * Tests for Java serialization/deserialization class.
 *
 * @author Aleksandr Uhanov
 * @since 2019-10-12
 */
public class JsonListTest {

    @Test
    public void jsonToListOfStringsTest() {
        List<String> list = Json.jsonToList("[\"Audi\",\"Ford\",\"Bayerische Motoren Werke \\\"BMW\\\"\"]");
        assertEquals(3, list.size());
        assertEquals("Audi", list.get(0));
        assertEquals("Ford", list.get(1));
        assertEquals("Bayerische Motoren Werke \"BMW\"", list.get(2));
    }

    @Test
    public void jsonToListOfStringsWithWhitespacesTest() {
        List<String> list = Json.jsonToList(" [\"Audi\" \n,\"Ford\"\t,  \"Bayerische Motoren Werke \\\"BMW\\\"\"  ] ");
        assertEquals(3, list.size());
        assertEquals("Audi", list.get(0));
        assertEquals("Ford", list.get(1));
        assertEquals("Bayerische Motoren Werke \"BMW\"", list.get(2));
    }

    @Test
    public void jsonToListOfNumbersTest() {
        List<String> list = Json.jsonToList("[100,-12345,0]");
        assertEquals(3, list.size());
        assertEquals("100", list.get(0));
        assertEquals("-12345", list.get(1));
        assertEquals("0", list.get(2));
    }

    @Test
    public void jsonToListOfNumbersWithWhitespacesTest() {
        List<String> list = Json.jsonToList("\n[\r\n100\t    ,-12345\t\t,0  ]");
        assertEquals(3, list.size());
        assertEquals("100", list.get(0));
        assertEquals("-12345", list.get(1));
        assertEquals("0", list.get(2));
    }

    @Test
    public void jsonToListOfArraysTest() {
        List<String> list = Json.jsonToList("[[1,2,3],[4,5],[-7,12,200,400]]");
        assertEquals(3, list.size());
        assertEquals("[1,2,3]", list.get(0));
        assertEquals("[4,5]", list.get(1));
        assertEquals("[-7,12,200,400]", list.get(2));
    }

    @Test
    public void jsonToListOfArraysWithWhitespacesTest() {
        List<String> list = Json.jsonToList("[\n\n[1,2,3]\r\n  ,[4,5 ]  ,[-7  ,12,200,\t\t400] ] ");
        assertEquals(3, list.size());
        assertEquals("[1,2,3]", list.get(0));
        assertEquals("[4,5 ]", list.get(1));
        assertEquals("[-7  ,12,200,\t\t400]", list.get(2));
    }

    @Test
    public void jsonToListOfObjectsTest() {
        List<String> list = Json.jsonToList("[{\"age\":35},{\"age\":44},{\"age\":67}]");
        assertEquals(3, list.size());
        assertEquals("{\"age\":35}", list.get(0));
        assertEquals("{\"age\":44}", list.get(1));
        assertEquals("{\"age\":67}", list.get(2));
    }

    @Test
    public void jsonToListOfBooleansTest() {
        List<String> list = Json.jsonToList("[true,false,true]");
        assertEquals(3, list.size());
        assertEquals("true", list.get(0));
        assertEquals("false", list.get(1));
        assertEquals("true", list.get(2));
    }

    @Test
    public void jsonToListOfNullsTest() {
        List<String> list = Json.jsonToList("[null,null,null]");
        assertEquals(3, list.size());
        assertEquals("null", list.get(0));
        assertEquals("null", list.get(1));
        assertEquals("null", list.get(2));
    }

    @Test
    public void jsonToEmptyListTest() {
        List<String> list = Json.jsonToList("[]");
        assertEquals(0, list.size());
    }
}
