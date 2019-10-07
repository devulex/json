package com.editbox.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * A Java serialization/deserialization class to convert Java Objects into JSON and back.
 *
 * @author Aleksandr Uhanov
 * @version 1.0.0
 * @since 2019-10-07
 */
public class Json {

    private static final char FIELD_SEPARATOR = ',';

    public static String format(Object source) {
        if (source == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(512);
        format0(source, builder);
        return builder.toString();
    }

    public static <T> T parse(String json, Class<T> valueType) {
        if (json == null) {
            return null;
        }
        if (valueType == null) {
            throw new RuntimeException("Parameter valueType cannot be null.");
        }
        try {
            return parse0(json, valueType, null);
        } catch (Exception e) {
            throw new RuntimeException("Json parse exception.", e);
        }
    }

    private static void format0(Object source, StringBuilder builder) {
        Class type = source.getClass();
        String typeName = type.getName();
        switch (typeName) {
            case "boolean":
            case "java.lang.Boolean":
            case "byte":
            case "java.lang.Byte":
            case "short":
            case "java.lang.Short":
            case "int":
            case "java.lang.Integer":
            case "long":
            case "java.lang.Long":
            case "float":
            case "java.lang.Float":
            case "double":
            case "java.lang.Double":
            case "java.math.BigInteger":
            case "java.math.BigDecimal":
                builder.append(source);
                return;
            case "java.lang.String":
                builder.append("\"").append(escapeString(source.toString())).append("\"");
                return;
            case "java.util.UUID":
                builder.append("\"").append(source.toString()).append("\"");
                return;
            case "java.util.Date":
                builder.append(((Date) source).getTime());
                return;
            case "java.time.LocalDate":
                builder.append("\"").append(((LocalDate) source).format(DateTimeFormatter.ISO_DATE)).append("\"");
                return;
            case "java.time.LocalTime":
                builder.append("\"").append(((LocalTime) source).format(DateTimeFormatter.ISO_TIME)).append("\"");
                return;
            case "java.time.LocalDateTime":
                builder.append("\"").append(((LocalDateTime) source).format(DateTimeFormatter.ISO_DATE_TIME)).append("\"");
                return;
            case "java.time.ZonedDateTime":
                builder.append("\"").append(((ZonedDateTime) source).format(DateTimeFormatter.ISO_ZONED_DATE_TIME)).append("\"");
                return;
        }
        if (typeName.startsWith("[") || source instanceof Collection) {
            formatList(source, typeName, builder);
            return;
        }
        if (source instanceof Map) {
            formatMap(source, builder);
            return;
        }
        formatFields(source, builder);
    }

    private static void formatList(Object source, String typeName, StringBuilder builder) {
        builder.append("[");
        if (typeName.startsWith("[")) {
            for (Object item : unpack(source)) {
                format0(item, builder);
                builder.append(FIELD_SEPARATOR);
            }
        } else {
            for (Object item : (Collection) source) {
                format0(item, builder);
                builder.append(FIELD_SEPARATOR);
            }
        }
        if (builder.charAt(builder.length() - 1) == FIELD_SEPARATOR) {
            builder.setCharAt(builder.length() - 1, ']');
        } else {
            builder.append("]");
        }
    }

    private static void formatMap(Object source, StringBuilder builder) {
        builder.append("{");
        for (Object object : ((Map) source).entrySet()) {
            Map.Entry entry = (Map.Entry) object;
            builder.append("\"").append(entry.getKey()).append("\"").append(":");
            format0(entry.getValue(), builder);
            builder.append(FIELD_SEPARATOR);
        }
        if (builder.charAt(builder.length() - 1) == FIELD_SEPARATOR) {
            builder.setCharAt(builder.length() - 1, '}');
        } else {
            builder.append("}");
        }
    }

    private static void formatFields(Object source, StringBuilder builder) {
        try {
            builder.append("{");
            Class clazz = source.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (field == null) {
                    continue;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                if (field.getAnnotation(JsonIgnore.class) != null) {
                    continue;
                }
                String name = field.getName();
                Object fieldValue = field.get(source);
                if (fieldValue == null && field.getAnnotation(JsonIncludeNull.class) == null) {
                    continue;
                }
                builder.append("\"").append(name).append("\"").append(":");
                if (fieldValue == null) {
                    builder.append("null");
                } else {
                    format0(fieldValue, builder);
                }
                builder.append(FIELD_SEPARATOR);
            }
            if (builder.charAt(builder.length() - 1) == FIELD_SEPARATOR) {
                builder.setCharAt(builder.length() - 1, '}');
            } else {
                builder.append("}");
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static String escapeString(String source) {
        StringBuilder builder = new StringBuilder(source.length() + source.length() / 10);
        for (char c : source.toCharArray()) {
            switch (c) {
                case '"':
                    builder.append("\\\"");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }

    private static Object[] unpack(Object array) {
        Object[] arrayOfObjects = new Object[Array.getLength(array)];
        for (int i = 0; i < arrayOfObjects.length; i++) {
            arrayOfObjects[i] = Array.get(array, i);
        }
        return arrayOfObjects;
    }

    private static <T> T parse0(String json, Class<T> type, Class<?> stringListClass) throws Exception {
        json = json.trim();
        String typeName = type.getName();
        switch (typeName) {
            case "boolean":
            case "java.lang.Boolean":
                return (T) Boolean.valueOf(json);
            case "short":
            case "java.lang.Short":
                return (T) Short.valueOf(json);
            case "int":
            case "java.lang.Integer":
                return (T) Integer.valueOf(json);
            case "long":
            case "java.lang.Long":
                return (T) Long.valueOf(json);
            case "float":
            case "java.lang.Float":
                return (T) Float.valueOf(json);
            case "double":
            case "java.lang.Double":
                return (T) Double.valueOf(json);
            case "java.lang.String":
                return (T) json;
        }
        /*if (typeName.startsWith("[")) {
            for (String value: jsonToList(json)) {

            }
            return ;
        }*/
        if (Collection.class.isAssignableFrom(type)) {
            List res = new ArrayList<>();
            for (String value: jsonToList(json)) {
                res.add(parse0(value, stringListClass, null));
            }
            return (T) res;
        }
        /*if (Map.class.isAssignableFrom(type)) {
            return parseMap(json, type);
        }*/
        return parseFields(json, type);
    }

    private static <T> T parseList(String json, Class<T> type) {
        return null; // TODO
    }

    private static <T> T parseMap(String json, Class<T> type) {
        return null; // TODO
    }

    private static <T> T parseFields(String json, Class<T> type) throws Exception {
        Map<String, String> pairs = jsonToPairs(json);
        T object;
        try {
            object = type.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No-argument constructor of " + type.getName() + " not found.");
        }
        for (Field field : type.getDeclaredFields()) {
            if (field == null) {
                continue;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            if (field.getAnnotation(JsonIgnore.class) != null) {
                continue;
            }
            String fieldName = field.getName();
            String value = pairs.get(fieldName);
            if (value != null) {
                Class<?> stringListClass = null;
                if (Collection.class.isAssignableFrom(field.getType())) {
                    ParameterizedType listTypes = (ParameterizedType) field.getGenericType();
                    stringListClass = (Class<?>) listTypes.getActualTypeArguments()[0];
                }
                field.set(object, parse0(value, field.getType(), stringListClass));
            }
        }
        return object;
    }

    public static List<String> jsonToList(String json) {
        List<String> list = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        int max = json.length();
        int pos = 0;
        int state = 0;
        int level = 0;
        while (pos < max) {
            char c = json.charAt(pos);
            switch (state) {
                case 0:
                    if (isWhitespace(c)) {
                        break;
                    }
                    if (c == '[') {
                        state = 1;
                        break;
                    }
                    throwParseException(c, pos);
                case 1:
                    if (isWhitespace(c)) {
                        break;
                    }
                    if (c == '"') {
                        state = 2;
                    } else if (isDigit(c) || c == '-') {
                        value.append(c);
                        state = 3;
                    } else if (c == '[') {
                        value.append(c);
                        level = 0;
                        state = 4;
                    } else if (c == '{') {
                        value.append(c);
                        level = 0;
                        state = 5;
                    } else if (c == 't') {
                        state = 6;
                    } else if (c == 'f') {
                        state = 7;
                    } else if (c == 'n') {
                        state = 8;
                    } else {
                        throwParseException(c, pos);
                    }
                    break;
                case 2: // string value
                    if (c == '\\') {
                        pos++;
                        value.append(json.charAt(pos));
                    } else if (c == '"') {
                        state = 9;
                        list.add(value.toString());
                    } else {
                        value.append(c);
                    }
                    break;
                case 3: // number value
                    if (isDigit(c)) {
                        value.append(c);
                    } else if (c == ',') {
                        list.add(value.toString());
                        state = 1;
                        value = new StringBuilder();
                    } else if (c == ']') {
                        list.add(value.toString());
                        state = 10;
                    } else if (isWhitespace(c)) {
                        list.add(value.toString());
                        state = 9;
                    } else {
                        throwParseException(c, pos);
                    }
                    break;
                case 4: // array value
                    value.append(c);
                    if (c == '[') {
                        level++;
                    } else if (c == ']') {
                        if (level == 0) {
                            list.add(value.toString());
                            state = 9;
                        } else {
                            level--;
                        }
                    }
                    break;
                case 5: // object value
                    value.append(c);
                    if (c == '{') {
                        level++;
                    } else if (c == '}') {
                        if (level == 0) {
                            list.add(value.toString());
                            state = 9;
                        } else {
                            level--;
                        }
                    }
                    break;
                case 6: // true boolean value
                    if (c == 'r' && json.charAt(pos + 1) == 'u' && json.charAt(pos + 2) == 'e') {
                        pos += 2;
                        state = 9;
                        value.append("true");
                        list.add(value.toString());
                        break;
                    }
                    throwParseException(c, pos);
                case 7: // false boolean value
                    if (c == 'a' && json.charAt(pos + 1) == 'l' && json.charAt(pos + 2) == 's' && json.charAt(pos + 3) == 'e') {
                        pos += 3;
                        state = 9;
                        value.append("false");
                        list.add(value.toString());
                        break;
                    }
                    throwParseException(c, pos);
                case 8: // null value
                    if (c == 'u' && json.charAt(pos + 1) == 'l' && json.charAt(pos + 2) == 'l') {
                        pos += 2;
                        state = 9;
                        value.append("null");
                        list.add(value.toString());
                        break;
                    }
                    throwParseException(c, pos);
                case 9:
                    if (isWhitespace(c)) {
                        break;
                    } else if (c == ',') {
                        state = 1;
                        value = new StringBuilder();
                        break;
                    } else if (c == ']') {
                        state = 10;
                        break;
                    } else {
                        throwParseException(c, pos);
                    }
                case 10:
                    if (isWhitespace(c)) {
                        break;
                    } else {
                        throwParseException(c, pos);
                    }
            }
            pos++;
        }
        return list;
    }

    public static Map<String, String> jsonToPairs(String json) {
        Map<String, String> map = new HashMap<>();
        StringBuilder attribute = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int max = json.length();
        int pos = 0;
        int state = 0;
        int level = 0;
        while (pos < max) {
            char c = json.charAt(pos);
            switch (state) {
                case 0: // start quotation mark
                    if (c == '"') {
                        state = 1;
                        attribute = new StringBuilder();
                        value = new StringBuilder();
                    }
                    break;
                case 1: // attribute name
                    if (c == '\\') {
                        break;
                    }
                    if (c == '"') {
                        state = 2;
                    } else {
                        attribute.append(c);
                    }
                    break;
                case 2: // colon
                    if (c == ':') {
                        state = 3;
                    }
                    break;
                case 3: // start value
                    if (c == '"') {
                        state = 4; // start string value
                    } else if (isDigit(c)) {
                        state = 5;  // start number value
                        value.append(c);
                    } else if (c == '[') {
                        state = 6; // start array value
                        level = 0;
                        value.append(c);
                    } else if (c == '{') {
                        state = 7; // start object value
                        level = 0;
                        value.append(c);
                    } else if (c == 't') {
                        state = 8; // start boolean value
                    } else if (c == 'f') {
                        state = 9; // start boolean value
                    } else if (c == 'n') {
                        state = 10; // null value
                    }
                    break;
                case 4: // string value
                    if (c == '\\') {
                        pos++;
                        value.append(json.charAt(pos));
                    } else if (c == '"') {
                        state = 0;
                        map.put(attribute.toString(), value.toString());
                    } else {
                        value.append(c);
                    }
                    break;
                case 5: // number value
                    if (isDigit(c)) {
                        value.append(c);
                    } else {
                        state = 0;
                        map.put(attribute.toString(), value.toString());
                    }
                    break;
                case 6: // array value
                    value.append(c);
                    if (c == '[') {
                        level++;
                    } else if (c == ']') {
                        if (level == 0) {
                            state = 0;
                            map.put(attribute.toString(), value.toString());
                        } else {
                            level--;
                        }
                    }
                    break;
                case 7: // object value
                    value.append(c);
                    if (c == '{') {
                        level++;
                    } else if (c == '}') {
                        if (level == 0) {
                            state = 0;
                            map.put(attribute.toString(), value.toString());
                        } else {
                            level--;
                        }
                    }
                    break;
                case 8: // true boolean value
                    if (c == 'r' && json.charAt(pos + 1) == 'u' && json.charAt(pos + 2) == 'e') {
                        pos += 2;
                        state = 0;
                        value.append("true");
                        map.put(attribute.toString(), value.toString());
                    } else {
                        throw new RuntimeException("Error parsing boolean value.");
                    }
                    break;
                case 9: // false boolean value
                    if (c == 'a' && json.charAt(pos + 1) == 'l' && json.charAt(pos + 2) == 's' && json.charAt(pos + 3) == 'e') {
                        pos += 3;
                        state = 0;
                        value.append("false");
                        map.put(attribute.toString(), value.toString());
                    } else {
                        throw new RuntimeException("Error parsing boolean value.");
                    }
                    break;
                case 10: // null value
                    if (c == 'u' && json.charAt(pos + 1) == 'l' && json.charAt(pos + 2) == 'l') {
                        pos += 2;
                        state = 0;
                        value.append("null");
                        map.put(attribute.toString(), value.toString());
                    } else {
                        throw new RuntimeException("Error parsing null value.");
                    }
                    break;
            }
            pos++;
        }
        return map;
    }

    private static void throwParseException(char c, int pos) {
        throw new RuntimeException("Unexpected token " + c + " in JSON at position " + pos);
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
