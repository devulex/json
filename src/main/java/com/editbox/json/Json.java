package com.editbox.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * A Java serialization/deserialization class to convert Java Objects into JSON and back.
 * <p>
 * Implemented RFC 8259.
 *
 * @author Aleksandr Uhanov
 * @version 1.0.0
 * @since 2019-10-12
 */
public class Json {

    private static final char FIELD_SEPARATOR = ',';

    /**
     * This method serializes the specified object into its equivalent Json representation.
     *
     * @param source the object for which Json representation is to be created setting for Json
     * @return Json representation of {@code source}.
     */
    public static String format(Object source) {
        if (source == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(512);
        format0(source, builder);
        return builder.toString();
    }

    /**
     * This method deserializes the specified Json into an object of the specified class.
     *
     * @param <T>       the type of the desired object
     * @param json      the string from which the object is to be deserialized
     * @param valueType the class of T
     * @return an object of type T from the string.
     * @throws JsonParseException if json is not a valid representation for an object of type valueType
     */
    public static <T> T parse(String json, Class<T> valueType) {
        if (json == null) {
            return null;
        }
        if (valueType == null) {
            throw new JsonParseException("Parameter valueType cannot be null.");
        }
        try {
            return parse0(json, valueType, null, null);
        } catch (Exception e) {
            throw new JsonParseException("Json parse exception.", e);
        }
    }

    private static void format0(Object source, StringBuilder builder) {
        Class type = source.getClass();
        if (source instanceof Number || source instanceof Boolean) {
            builder.append(source);
            return;
        }
        if (source instanceof String) {
            builder.append("\"").append(escapeString(source.toString())).append("\"");
            return;
        }
        if (source instanceof Temporal || source instanceof UUID) {
            builder.append("\"").append(source).append("\"");
            return;
        }
        if (source instanceof Date) {
            builder.append(((Date) source).getTime());
            return;
        }
        if (type.isArray() || source instanceof Collection) {
            formatList(source, type, builder);
            return;
        }
        if (source instanceof Map) {
            formatMap(source, builder);
            return;
        }
        if (type.isEnum()) {
            builder.append("\"").append(source).append("\"");
            return;
        }
        formatFields(source, builder);
    }

    private static void formatList(Object source, Class type, StringBuilder builder) {
        builder.append("[");
        if (type.isArray()) {
            for (Object item : unpack(source)) {
                if (item == null) {
                    continue;
                }
                format0(item, builder);
                builder.append(FIELD_SEPARATOR);
            }
        } else {
            for (Object item : (Collection) source) {
                if (item == null) {
                    continue;
                }
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
            if (entry.getValue() == null) {
                continue;
            }
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
            throw new JsonParseException(e);
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
            case "java.math.BigInteger":
                return (T) new BigInteger(json);
            case "java.math.BigDecimal":
                return (T) new BigDecimal(json);
            case "java.lang.String":
                return (T) json;
            case "java.util.UUID":
                return (T) UUID.fromString(json);
            case "java.util.Date":
                return (T) new Date(Long.parseLong(json));
            case "java.time.LocalDate":
                return (T) LocalDate.parse(json);
            case "java.time.LocalTime":
                return (T) LocalTime.parse(json);
            case "java.time.LocalDateTime":
                return (T) LocalDateTime.parse(json);
            case "java.time.ZonedDateTime":
                return (T) ZonedDateTime.parse(json);
        }
        if (typeName.startsWith("[")) {
            List<String> values = jsonToList(json);
            int length = values.size();
            Object array = Array.newInstance(parameterizedType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, parse0(values.get(i), parameterizedType, null, null));
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
            throw new JsonParseException("No-argument constructor of " + type.getName() + " not found.");
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
            if (value != null && !value.equals("null")) {
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
        return jsonToMapOrList(json, false, '{', '}').map;
    }

    public static List<String> jsonToList(String json) {
        return jsonToMapOrList(json, true, '[', ']').list;
    }

    private static ParseResult jsonToMapOrList(String json, boolean isArray, char beginChar, char endChar) {
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
                    if (c == endChar) {
                        state = 13;
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
                    } else if (json.substring(pos, pos + 4).equalsIgnoreCase("true")) {
                        value.append("true");
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                        pos += 3;
                        state = 12;
                    } else if (json.substring(pos, pos + 5).equalsIgnoreCase("false")) {
                        value.append("false");
                        if (isArray) {
                            list.add(value.toString());
                        } else {
                            map.put(attribute.toString(), value.toString());
                        }
                        pos += 4;
                        state = 12;
                    } else if (json.substring(pos, pos + 4).equalsIgnoreCase("null")) {
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
                        c = json.charAt(pos);
                        switch (c) {
                            case '"':
                                value.append('"');
                                break;
                            case '\\':
                                value.append('\\');
                                break;
                            case '/':
                                value.append('/');
                                break;
                            case 'b':
                                value.append('\b');
                                break;
                            case 'f':
                                value.append('\f');
                                break;
                            case 'n':
                                value.append('\n');
                                break;
                            case 'r':
                                value.append('\r');
                                break;
                            case 't':
                                value.append('\t');
                                break;
                            case 'u':
                                int code = Integer.parseInt(json.substring(pos + 1, pos + 5), 16);
                                char unicodeChar = Character.toChars(code)[0];
                                value.append(unicodeChar);
                                pos += 4;
                                break;
                            default:
                                throwParseException(c, pos);
                                break;
                        }
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
                    if (isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
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
            throw new JsonParseException("Unexpected end of JSON");
        }
        return isArray ? ParseResult.fromList(list) : ParseResult.fromMap(map);
    }

    private static void throwParseException(char c, int pos) {
        throw new JsonParseException("Unexpected token " + c + " in JSON at position " + pos);
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

    public static class JsonParseException extends RuntimeException {

        public JsonParseException() {
            super();
        }

        public JsonParseException(String s) {
            super(s);
        }

        public JsonParseException(String message, Throwable cause) {
            super(message, cause);
        }

        public JsonParseException(Throwable cause) {
            super(cause);
        }
    }
}
