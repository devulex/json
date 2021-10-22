package com.editbox.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Test entity with fields of different types.
 *
 * @author Aleksandr Uhanov
 * @since 2019-10-12
 */
public class Entity {

    private final static String staticField = "staticFieldValue";

    private String nullField;

    private boolean primitiveBoolean;

    private Boolean objectBoolean;

    private byte primitiveByte;

    private Byte objectByte;

    private short primitiveShort;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private transient Short objectShort;

    private int primitiveInt;

    private Integer objectInt;

    private long primitiveLong;

    private Long objectLong;

    private float primitiveFloat;

    private Float objectFloat;

    private double primitiveDouble;

    private Double objectDouble;

    private BigInteger bigInteger;

    private BigDecimal bigDecimal;

    private String string;

    private String stringWithSpecChars;

    private UUID uuid;

    private Date date;

    //private LocalDate localDate;

    //private LocalTime localTime;

    //private LocalDateTime localDateTime;

    //private ZonedDateTime zonedDateTime;

    private String[] array;

    private List<String> list;

    private Map<String, Integer> map;

    public String getNullField() {
        return nullField;
    }

    public void setNullField(String nullField) {
        this.nullField = nullField;
    }

    public boolean isPrimitiveBoolean() {
        return primitiveBoolean;
    }

    public void setPrimitiveBoolean(boolean primitiveBoolean) {
        this.primitiveBoolean = primitiveBoolean;
    }

    public Boolean getObjectBoolean() {
        return objectBoolean;
    }

    public void setObjectBoolean(Boolean objectBoolean) {
        this.objectBoolean = objectBoolean;
    }

    public byte getPrimitiveByte() {
        return primitiveByte;
    }

    public void setPrimitiveByte(byte primitiveByte) {
        this.primitiveByte = primitiveByte;
    }

    public Byte getObjectByte() {
        return objectByte;
    }

    public void setObjectByte(Byte objectByte) {
        this.objectByte = objectByte;
    }

    public short getPrimitiveShort() {
        return primitiveShort;
    }

    public void setPrimitiveShort(short primitiveShort) {
        this.primitiveShort = primitiveShort;
    }

    public Short getObjectShort() {
        return objectShort;
    }

    public void setObjectShort(Short objectShort) {
        this.objectShort = objectShort;
    }

    public int getPrimitiveInt() {
        return primitiveInt;
    }

    public void setPrimitiveInt(int primitiveInt) {
        this.primitiveInt = primitiveInt;
    }

    public Integer getObjectInt() {
        return objectInt;
    }

    public void setObjectInt(Integer objectInt) {
        this.objectInt = objectInt;
    }

    public long getPrimitiveLong() {
        return primitiveLong;
    }

    public void setPrimitiveLong(long primitiveLong) {
        this.primitiveLong = primitiveLong;
    }

    public Long getObjectLong() {
        return objectLong;
    }

    public void setObjectLong(Long objectLong) {
        this.objectLong = objectLong;
    }

    public float getPrimitiveFloat() {
        return primitiveFloat;
    }

    public void setPrimitiveFloat(float primitiveFloat) {
        this.primitiveFloat = primitiveFloat;
    }

    public Float getObjectFloat() {
        return objectFloat;
    }

    public void setObjectFloat(Float objectFloat) {
        this.objectFloat = objectFloat;
    }

    public double getPrimitiveDouble() {
        return primitiveDouble;
    }

    public void setPrimitiveDouble(double primitiveDouble) {
        this.primitiveDouble = primitiveDouble;
    }

    public Double getObjectDouble() {
        return objectDouble;
    }

    public void setObjectDouble(Double objectDouble) {
        this.objectDouble = objectDouble;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getStringWithSpecChars() {
        return stringWithSpecChars;
    }

    public void setStringWithSpecChars(String stringWithSpecChars) {
        this.stringWithSpecChars = stringWithSpecChars;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
//
//    public LocalDate getLocalDate() {
//        return localDate;
//    }
//
//    public void setLocalDate(LocalDate localDate) {
//        this.localDate = localDate;
//    }
//
//    public LocalTime getLocalTime() {
//        return localTime;
//    }
//
//    public void setLocalTime(LocalTime localTime) {
//        this.localTime = localTime;
//    }
//
//    public LocalDateTime getLocalDateTime() {
//        return localDateTime;
//    }
//
//    public void setLocalDateTime(LocalDateTime localDateTime) {
//        this.localDateTime = localDateTime;
//    }

//    public ZonedDateTime getZonedDateTime() {
//        return zonedDateTime;
//    }
//
//    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
//        this.zonedDateTime = zonedDateTime;
//    }

    public static String getStaticField() {
        return staticField;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}
