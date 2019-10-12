package com.editbox.json;

import com.editbox.json.entity.Computer;
import org.junit.Test;
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
        Computer computer = new Computer("Intel super server", "12345678");
        assertEquals("{\"name\":\"Intel super server\"}", Json.format(computer));
    }
}
