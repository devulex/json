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
 * <p>
 * Implemented RFC 8259.
 *
 * @author Aleksandr Uhanov
 * @version 1.0.0
 * @since 2019-10-10
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
            return parse0(json, valueType, null, null);
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
                String name = field.getName();
                Object fieldValue = field.get(source);
                if (fieldValue == null) {
                    continue;
                }
                builder.append("\"").append(name).append("\"").append(":");
                format0(fieldValue, builder);
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

    private static <T> T parse0(String json, Class<T> type, Class<?> parameterizedType, Class<?> parameterizedTypeMap) throws Exception {
        String typeName = type.getName();
        switch (typeName) {
            case "boolean":
            case "java.lang.Boolean":
                return (T) Boolean.valueOf(json);
            case "byte":
            case "java.lang.Byte":
                return (T) Byte.valueOf(json);
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
            case "java.util.UUID":
                return (T) UUID.fromString(json);
        }
        if (typeName.startsWith("[")) {
            List<String> values = jsonToList(json);
            int length = values.size();
            Object array = Array.newInstance(parameterizedType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, values.get(i));
            }
            return (T) array;
        }
        if (Collection.class.isAssignableFrom(type)) {
            List list = new ArrayList<>();
            for (String value : jsonToList(json)) {
                list.add(parse0(value, parameterizedType, null, null));
            }
            return (T) list;
        }
        if (Map.class.isAssignableFrom(type)) {
            Map map = new HashMap<>();
            for (Map.Entry<String, String> entry : jsonToMap(json).entrySet()) {
                map.put(entry.getKey(), parse0(entry.getValue(), parameterizedTypeMap, null, null));
            }
            return (T) map;
        }
        return parseFields(json, type);
    }

    private static <T> T parseFields(String json, Class<T> type) throws Exception {
        Map<String, String> pairs = jsonToMap(json);
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
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String value = pairs.get(fieldName);
            if (value != null) {
                Class<?> parameterizedType = null;
                Class<?> parameterizedTypeMap = null;
                if (fieldType.isArray()) {
                    parameterizedType = fieldType.getComponentType();
                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    ParameterizedType listTypes = (ParameterizedType) field.getGenericType();
                    parameterizedType = (Class<?>) listTypes.getActualTypeArguments()[0];
                } else if (Map.class.isAssignableFrom(field.getType())) {
                    ParameterizedType listTypes = (ParameterizedType) field.getGenericType();
                    parameterizedType = (Class<?>) listTypes.getActualTypeArguments()[0];
                    parameterizedTypeMap = (Class<?>) listTypes.getActualTypeArguments()[1];
                }
                field.set(object, parse0(value, fieldType, parameterizedType, parameterizedTypeMap));
            }
        }
        return object;
    }

    public static Map<String, String> jsonToMap(String json) {
        return jsonToX(json, false, '{', '}').map;
    }

    public static List<String> jsonToList(String json) {
        return jsonToX(json, true, '[', ']').list;
    }

    private static ParseResult jsonToX(String json, boolean isArray, char beginChar, char endChar) {
        Map<String, String> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        StringBuilder attribute = new StringBuilder();
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
                    if (c == beginChar) {
                        state = isArray ? 4 : 1;
                        break;
                    }
                    throwParseException(c, pos);
                case 1:
                    if (isWhitespace(c)) {
                        break;
                    }
                    if (c == '"') {
                        state = 2;
                        break;
                    }
                    throwParseException(c, pos);
                case 2:
                    if (isLiteralName(c)) {
                        attribute.append(c);
                        break;
                    }
                    if (c == '"') {
                        state = 3;
                        break;
                    }
                    throwParseException(c, pos);
                case 3:
                    if (isWhitespace(c)) {
                        break;
                    }
                    if (c == ':') {
                        state = 4;
                        break;
                    }
                    throwParseException(c, pos);
                case 4:
                    if (isWhitespace(c)) {
                        break;
                    }
                    if (c == '"') {
                        state = 5;
                    } else if (isDigit(c) || c == '-') {
                        value.append(c);
                        state = 6;
                    } else if (c == '[') {
                        value.append(c);
                        level = 0;
                        state = 7;
                    } else if (c == '{') {
                        value.append(c);
                        level = 0;
                        state = 8;
                    } else if (c == 't' && json.charAt(pos + 1) == 'r' && json.charAt(pos + 2) == 'u' && json.charAt(pos + 3) == 'e') {
                        value.append("true");
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                        pos += 3;
                        state = 12;
                    } else if (c == 'f') {
                        value.append("false");
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                        pos += 4;
                        state = 12;
                    } else if (c == 'n' && json.charAt(pos + 1) == 'u' && json.charAt(pos + 2) == 'l' && json.charAt(pos + 3) == 'l') {
                        value.append("null");
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                        pos += 3;
                        state = 12;
                    } else {
                        throwParseException(c, pos);
                    }
                    break;
                case 5: // string value
                    if (c == '\\') {
                        pos++;
                        value.append(json.charAt(pos));
                    } else if (c == '"') {
                        state = 12;
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                    } else {
                        value.append(c);
                    }
                    break;
                case 6: // number value
                    if (isDigit(c) || c == '.') {
                        value.append(c);
                    } else if (c == ',') {
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                        state = isArray ? 4 : 1;
                        attribute = new StringBuilder();
                        value = new StringBuilder();
                    } else if (c == endChar) {
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                        state = 13;
                    } else if (isWhitespace(c)) {
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                        state = 12;
                    } else {
                        throwParseException(c, pos);
                    }
                    break;
                case 7: // array value
                    value.append(c);
                    if (c == '[') {
                        level++;
                    } else if (c == ']') {
                        if (level == 0) {
                            if (isArray) {
                                list.add(value.toString());
                            } else {
                                map.put(attribute.toString(), value.toString());
                            }
                            state = 12;
                        } else {
                            level--;
                        }
                    }
                    break;
                case 8: // object value
                    value.append(c);
                    if (c == '{') {
                        level++;
                    } else if (c == '}') {
                        if (level == 0) {
                            if (isArray) {
                                list.add(value.toString());
                            } else {
                                map.put(attribute.toString(), value.toString());
                            }
                            state = 12;
                        } else {
                            level--;
                        }
                    }
                    break;
                case 12:
                    if (isWhitespace(c)) {
                        break;
                    } else if (c == ',') {
                        state = isArray ? 4 : 1;
                        attribute = new StringBuilder();
                        value = new StringBuilder();
                        break;
                    } else if (c == endChar) {
                        state = 13;
                        break;
                    } else {
                        throwParseException(c, pos);
                    }
                case 13:
                    if (isWhitespace(c)) {
                        break;
                    } else {
                        throwParseException(c, pos);
                    }
            }
            pos++;
        }
        if (state != 13) {
            throw new RuntimeException("Unexpected end of JSON");
        }
        return isArray ? ParseResult.fromList(list) : ParseResult.fromMap(map);
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

    private static boolean isLiteralName(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '-';
    }

    private static class ParseResult {

        Map<String, String> map;
        List<String> list;

        private ParseResult(Map<String, String> map, List<String> list) {
            this.map = map;
            this.list = list;
        }

        static ParseResult fromMap(Map<String, String> map) {
            return new ParseResult(map, null);
        }

        static ParseResult fromList(List<String> list) {
            return new ParseResult(null, list);
        }
    }
}
