package com.editbox.json;

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

import static org.junit.Assert.assertEquals;

/**
 * tests for Java serialization/deserialization class.
 *
 * @author Aleksandr Uhanov
 * @since 2019-09-28
 */
public class JsonTest {

    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void jsonBooleanTest() {
        Entity object = new Entity();
        object.setObjectBoolean(true);
        object.setPrimitiveBoolean(false);
        assertEquals(new Gson().toJson(object), Json.format(object));
    }

    @Test
    public void jsonAllFieldsTest() throws JsonProcessingException {
        Entity object = createTestJsonEntity();
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
        object.setPrimitiveFloat(3.1415929f);
        object.setObjectFloat(3.1415929f);
        object.setPrimitiveDouble(3.141592653589793);
        object.setBigInteger(BigInteger.valueOf(299_792_458));
        object.setBigDecimal(BigDecimal.valueOf(3.141592653589793));
        object.setObjectDouble(3.141592653589793);
        object.setString("Hello world!");
        object.setStringWithSpecChars("Spec chars: \" \\ / \b \f \n \r \t");
        object.setUuid(UUID.randomUUID());
        object.setDate(new Date(1537967663953L));
        LocalDateTime dateTime = LocalDateTime.of(2018, 9, 26, 16, 15, 57, 469713000);
        object.setLocalDate(dateTime.toLocalDate());
        object.setLocalTime(dateTime.toLocalTime());
        object.setLocalDateTime(dateTime);
        object.setZonedDateTime(dateTime.atZone(ZoneId.systemDefault()));
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
