package com.editbox.json;

import com.editbox.json.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Tests for Java serialization/deserialization class.
 *
 * @author Aleksandr Uhanov
 * @since 2019-10-07
 */
public class JsonTest {

    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void formatBooleanTest() {
        Entity object = new Entity();
        object.setObjectBoolean(true);
        object.setPrimitiveBoolean(false);
        assertEquals(new Gson().toJson(object), Json.format(object));
    }

    @Test
    public void formatAllFieldsTest() throws JsonProcessingException {
        Entity object = createTestJsonEntity();
        System.out.println("The JSON:     " + Json.format(object));
        System.out.println("Gson JSON:    " + new Gson().toJson(object));
        System.out.println("Jackson JSON: " + mapper.writeValueAsString(object));
    }

    @Test
    public void formatListTest() throws JsonProcessingException {
        Object object = Stream.of("One", "Two", "Three").collect(Collectors.toList());
        System.out.println("The JSON:     " + Json.format(object));
        System.out.println("Gson JSON:    " + new Gson().toJson(object));
        System.out.println("Jackson JSON: " + mapper.writeValueAsString(object));
    }

    @Test
    public void performanceTheJsonTest() {
        Entity object = createTestJsonEntity();
        int SIZE = 1000_000;
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            Json.format(object);
        }
        System.out.println("Serialization duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }

    @Test
    public void performanceGsonTest() {
        Entity object = createTestJsonEntity();
        int SIZE = 1000_000;
        long start = System.nanoTime();
        Gson gson = new Gson();
        for (int i = 0; i < SIZE; i++) {
            gson.toJson(object);
        }
        System.out.println("Serialization duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }

    @Test
    public void parseBooleanIntStringTest() {
        String json = "{\"primitiveBoolean\": true, \"primitiveInt\": 123, \"objectInt\": 456, \"string\": \"Hello world!\"}";
        Entity object = Json.parse(json, Entity.class);
        assertTrue(object.isPrimitiveBoolean());
        assertEquals(123, object.getPrimitiveInt());
        assertEquals(456, (int) object.getObjectInt());
        assertEquals("Hello world!", object.getString());
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
    public void formatAndParseTheSame() {
        Entity original = createTestJsonEntity();
        String json = Json.format(original);
        Entity parsed = Json.parse(json, Entity.class);
        assertNull(original.getNullField());
        assertEquals(original.isPrimitiveBoolean(), parsed.isPrimitiveBoolean());
        assertEquals(original.getObjectBoolean(), parsed.getObjectBoolean());
        assertEquals(original.getPrimitiveByte(), parsed.getPrimitiveByte());
        assertEquals(original.getObjectByte(), parsed.getObjectByte());
        assertEquals(original.getPrimitiveShort(), parsed.getPrimitiveShort());
        assertNull(parsed.getObjectShort());
        assertEquals(original.getPrimitiveInt(), parsed.getPrimitiveInt());
        assertEquals(original.getObjectInt(), parsed.getObjectInt());
        assertEquals(original.getPrimitiveLong(), parsed.getPrimitiveLong());
        assertEquals(original.getObjectLong(), parsed.getObjectLong());
        assertEquals(original.getString(), parsed.getString());
        //assertEquals(original.getStringWithSpecChars(), parsed.getStringWithSpecChars()); // TODO
        assertEquals(original.getUuid(), parsed.getUuid());
        assertArrayEquals(original.getArray(), parsed.getArray());
        assertEquals(original.getList(), parsed.getList());
        assertEquals(original.getMap(), parsed.getMap());
    }

    @Test
    public void jsonToPairsTest() {
        Json.jsonToMap("{\"firstName\" : \"John\", \"isAlive\": true,\"age\":27, \"phones\": [[123], [456]] , \"spouse\": null}");
    }

    @Test
    public void jsonToPairsPerformanceTest() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000_000; i++) {
            Json.jsonToMap("{\"firstName\": \"Elon\", \"lastName\": \"Musk\",\"age\":48, \"single\": true, \"balance\": 800.500}");
        }
        System.out.println("Duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }

    @Test
    public void jsonToPairsPerformanceGsonTest() {
        long start = System.nanoTime();
        Gson gson = new Gson();
        for (int i = 0; i < 1000_000; i++) {
            gson.fromJson("{\"firstName\": \"Elon\", \"lastName\": \"Musk\",\"age\":48, \"single\": true, \"balance\": 800.500}", Person.class);
        }
        System.out.println("Duration " + (System.nanoTime() - start) / 1000_000d + "ms");
    }

    private Entity createTestJsonEntity() {
        Entity object = new Entity();
        object.setObjectBoolean(true);
        object.setPrimitiveBoolean(false);
        object.setPrimitiveByte((byte) 111);
        object.setObjectByte((byte) 111);
        object.setPrimitiveShort((short) 22222);
        object.setObjectShort((short) 22222);
        object.setPrimitiveInt(222222222);
        object.setObjectInt(222222222);
        object.setPrimitiveLong(2222222222222222222L);
        object.setObjectLong(2222222222222222222L);
        //object.setPrimitiveFloat(3.1415929f);
        //object.setObjectFloat(3.1415929f);
        //object.setPrimitiveDouble(3.141592653589793);
        //object.setBigInteger(BigInteger.valueOf(299_792_458));
        //object.setBigDecimal(BigDecimal.valueOf(3.141592653589793));
        //object.setObjectDouble(3.141592653589793);
        object.setString("Hello world!");
        object.setStringWithSpecChars("Spec chars: \" \\ / \b \f \n \r \t");
        object.setUuid(UUID.randomUUID());
        //object.setDate(new Date(1537967663953L));
        //LocalDateTime dateTime = LocalDateTime.of(2018, 9, 26, 16, 15, 57, 469713000);
        //object.setLocalDate(dateTime.toLocalDate());
        //object.setLocalTime(dateTime.toLocalTime());
        //object.setLocalDateTime(dateTime);
        //object.setZonedDateTime(dateTime.atZone(ZoneId.systemDefault()));
        object.setArray(new String[]{"First", "Second", "Third"});
        object.setList(Stream.of("One", "Two", "Three").collect(Collectors.toList()));
        object.setMap(new HashMap<String, Integer>() {{
            put("One", 1);
            put("Two", 2);
            put("Three", 3);
        }});
        return object;
    }
}
