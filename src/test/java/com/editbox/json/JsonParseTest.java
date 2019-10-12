package com.editbox.json;

import com.editbox.json.entity.Person;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Java serialization/deserialization class.
 *
 * @author Aleksandr Uhanov
 * @since 2019-10-12
 */
public class JsonParseTest {

    @Test
    public void parseBooleanTrueTest() {
        String json = "{\"single\": true}";
        Person person = Json.parse(json, Person.class);
        assertTrue(person.isSingle());
    }

    @Test
    public void parseBooleanTrueUpperCaseTest() {
        String json = "{\"single\": TRUE}";
        Person person = Json.parse(json, Person.class);
        assertTrue(person.isSingle());
    }

    @Test
    public void parseBooleanTrueDiffCaseTest() {
        String json = "{\"single\": True}";
        Person person = Json.parse(json, Person.class);
        assertTrue(person.isSingle());
    }

    @Test
    public void parseBooleanFalseTest() {
        String json = "{\"single\": false}";
        Person person = Json.parse(json, Person.class);
        assertFalse(person.isSingle());
    }

    @Test
    public void parseBooleanFalseUpperCaseTest() {
        String json = "{\"single\": FALSE}";
        Person person = Json.parse(json, Person.class);
        assertFalse(person.isSingle());
    }

    @Test
    public void parseBooleanFalseDiffCaseTest() {
        String json = "{\"single\": False}";
        Person person = Json.parse(json, Person.class);
        assertFalse(person.isSingle());
    }

    @Test
    public void parseNullTest() {
        String json = "{\"name\": \"Alex\", \"phoneNumbers\": null}";
        Person person = Json.parse(json, Person.class);
        assertEquals("Alex", person.getName());
        assertNull(person.getPhoneNumbers());
    }

    @Test
    public void parseNullUpperCaseTest() {
        String json = "{\"name\": \"Alex\", \"phoneNumbers\": NULL}";
        Person person = Json.parse(json, Person.class);
        assertEquals("Alex", person.getName());
        assertNull(person.getPhoneNumbers());
    }

    @Test
    public void parseNullDiffCaseTest() {
        String json = "{\"name\": \"Alex\", \"phoneNumbers\": Null}";
        Person person = Json.parse(json, Person.class);
        assertEquals("Alex", person.getName());
        assertNull(person.getPhoneNumbers());
    }

    @Test
    public void parseArrayOfStringTest() {
        String json = "{\"primitiveBoolean\": true, \"array\": [\"Ford\", \"Audi\"]}";
        Entity object = Json.parse(json, Entity.class);
        assertTrue(object.isPrimitiveBoolean());
        assertEquals(2, object.getArray().length);
        assertEquals("Ford", object.getArray()[0]);
        assertEquals("Audi", object.getArray()[1]);
    }

    @Test
    public void parseListOfStringTest() {
        String json = "{\"primitiveBoolean\": true, \"list\": [\"Ford\", \"Audi\"]}";
        Entity object = Json.parse(json, Entity.class);
        assertTrue(object.isPrimitiveBoolean());
        assertEquals(2, object.getList().size());
        assertEquals("Ford", object.getList().get(0));
        assertEquals("Audi", object.getList().get(1));
    }

    @Test
    public void parseMapOfStringTest() {
        String json = "{\"primitiveBoolean\": true, \"map\": {\"size\": 10, \"count\": 20}}";
        Entity object = Json.parse(json, Entity.class);
        assertTrue(object.isPrimitiveBoolean());
        assertEquals(2, object.getMap().keySet().size());
        assertEquals(10, (int) object.getMap().get("size"));
        assertEquals(20, (int) object.getMap().get("count"));
    }

    @Test
    public void parseUnicodeCharTest() {
        String json = "{\"name\": \"Ale\\u0078\"}";
        Person person = Json.parse(json, Person.class);
        assertEquals("Alex", person.getName());
    }
}
