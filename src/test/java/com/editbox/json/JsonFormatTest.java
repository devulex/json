package com.editbox.json;

import com.editbox.json.entity.Computer;
import com.editbox.json.entity.Person;
import com.editbox.json.entity.PhoneNumber;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Tests for Java serialization/deserialization class.
 *
 * @author Aleksandr Uhanov
 * @since 2019-10-12
 */
public class JsonFormatTest {

    @Test
    public void formatTransientTest() {
        Computer computer = new Computer("Intel", "12345678");
        assertEquals("{\"name\":\"Intel\"}", Json.format(computer));
    }

    @Test
    public void formatArrayTest() {
        Person person = new Person();
        PhoneNumber number1 = new PhoneNumber("home", "212 555-1234");
        PhoneNumber number2 = new PhoneNumber("office", "646 555-4567");
        PhoneNumber number3 = new PhoneNumber("mobile", "123 456-7890");
        person.setPhoneNumbers(Arrays.asList(number1, number2, number3));
        assertEquals("{\"age\":0,\"single\":false,\"phoneNumbers\":" +
                "[{\"type\":\"home\",\"number\":\"212 555-1234\"}," +
                "{\"type\":\"office\",\"number\":\"646 555-4567\"}," +
                "{\"type\":\"mobile\",\"number\":\"123 456-7890\"}],\"balance\":0.0}", Json.format(person));
    }
}
