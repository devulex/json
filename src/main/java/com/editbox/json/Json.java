package com.editbox.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * A Java serialization/deserialization class to convert Java Objects into JSON and back.
 *
 * @author Aleksandr Uhanov
 * @version 1.0.0
 * @since 2019-09-28
 */
public class Json {

    private static final char FIELD_SEPARATOR = ',';

    public static String format(Object source) {
        if (source == null) {
            return null;
        }
        Class type = source.getClass();
        String typeName = type.getName();
        StringBuilder builder = new StringBuilder(512);
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
                break;
            case "java.lang.String":
                builder.append("\"").append(escapeString(source.toString())).append("\"");
                break;
            case "java.util.UUID":
                builder.append("\"").append(source.toString()).append("\"");
                break;
            case "java.util.Date":
                builder.append(((Date) source).getTime());
                break;
            case "java.time.LocalDate":
                builder.append("\"").append(((LocalDate) source).format(DateTimeFormatter.ISO_DATE)).append("\"");
                break;
            case "java.time.LocalTime":
                builder.append("\"").append(((LocalTime) source).format(DateTimeFormatter.ISO_TIME)).append("\"");
                break;
            case "java.time.LocalDateTime":
                builder.append("\"").append(((LocalDateTime) source).format(DateTimeFormatter.ISO_DATE_TIME)).append("\"");
                break;
            case "java.time.ZonedDateTime":
                builder.append("\"").append(((ZonedDateTime) source).format(DateTimeFormatter.ISO_ZONED_DATE_TIME)).append("\"");
                break;
            default:
                builder.append(formatNested(source));
                break;
        }
        return builder.toString();
    }

    private static String formatNested(Object source) {
        StringBuilder builder = new StringBuilder();
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
                    Class type = field.getType();
                    String typeName = type.getName();
                    if (typeName.startsWith("[") || fieldValue instanceof Collection) {
                        builder.append("[");
                        if (typeName.startsWith("[")) {
                            for (Object item : unpack(fieldValue)) {
                                builder.append(format(item));
                                builder.append(FIELD_SEPARATOR);
                            }
                        } else {
                            for (Object item : (Collection) fieldValue) {
                                builder.append(format(item));
                                builder.append(FIELD_SEPARATOR);
                            }
                        }
                        if (builder.charAt(builder.length() - 1) == FIELD_SEPARATOR) {
                            builder.setCharAt(builder.length() - 1, ']');
                        } else {
                            builder.append("]");
                        }
                    } else if (fieldValue instanceof Map) {
                        builder.append("{");
                        for (Object object : ((Map) fieldValue).entrySet()) {
                            Map.Entry entry = (Map.Entry) object;
                            builder.append("\"").append(entry.getKey()).append("\"").append(":");
                            builder.append(format(entry.getValue()));
                            builder.append(FIELD_SEPARATOR);
                        }
                        if (builder.charAt(builder.length() - 1) == FIELD_SEPARATOR) {
                            builder.setCharAt(builder.length() - 1, '}');
                        } else {
                            builder.append("}");
                        }
                    } else {
                        builder.append(format(fieldValue));
                    }
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
        return builder.toString();
    }

    public static <T> T parse(String json, Class<T> classOfType) {
        throw new UnsupportedOperationException("Parse not supported yet.");
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
}
