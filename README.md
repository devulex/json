# Json
Json is a Java library that can be used to convert Java Objects into their JSON representation.
It can also be used to convert a JSON string to an equivalent Java object.
Json can work with arbitrary Java objects including pre-existing objects that you do not have source-code of.
The library compatible with Java 8.

### Goals
  * Provide simple `format()` and `parse()` methods to convert Java objects to JSON and vice-versa
  * Allow pre-existing unmodifiable objects to be converted to and from JSON
  * Extensive support of Java Generics
  * Allow custom representations for objects
  * Support arbitrarily complex objects (with deep inheritance hierarchies and extensive use of generic types)

### Download

Just copy class `com.editbox.json.Json` to your project.

### Example
A simple Java object for testing.
```java
public class Staff {
    private String name;
    private int age;
    private boolean single;
    private List<String> skills;
    private Map<String, BigDecimal> salary;
    // getters, setters, some boring stuff
}
```
### Java Objects to JSON
```java
package com.editbox.json;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonExample {

    public static void main(String[] args) {
        Staff staff = createStaff();
        String json = Json.format(staff);
        System.out.println(json);
    }

    private static Staff createStaff() {
        Staff staff = new Staff();
        staff.setName("Elon Musk");
        staff.setAge(48);
        staff.setSingle(true);
        Map<String, BigDecimal> salary = new HashMap() {{
            put("2017", new BigDecimal(10000));
            put("2018", new BigDecimal(12000));
            put("2019", new BigDecimal(14000));
        }};
        staff.setSalary(salary);
        staff.setSkills(Arrays.asList("java", "python", "node", "kotlin"));
        return staff;
    }
}
```
Output
```
{"name":"Elon Musk","age":48,"single":true,"skills":["java","python","node","kotlin"],"salary":{"2019":14000,"2018":12000,"2017":10000}}
```
Output format
```json
{
    "name": "Elon Musk",
    "age": 48,
    "single": true,
    "skills": ["java", "python", "node", "kotlin"],
    "salary": {
        "2019": 14000,
        "2018": 12000,
        "2017": 10000
    }
}
```
### JSON to Java Object
```java
package com.editbox.json;

public class JsonExample {

    public static void main(String[] args) {

        String json = "{\"name\":\"Elon Musk\",\"age\":48,\"single\":true,\"skills\":" +
         "[\"java\",\"python\",\"node\",\"kotlin\"],\"salary\":{\"2019\":14000,\"2018\":12000,\"2017\":10000}}";

        Staff staff = Json.parse(json, Staff.class);

        System.out.println(staff.getName());
        System.out.println(staff.getAge());
        System.out.println(staff.isSingle());
        System.out.println(staff.getSkills());
        System.out.println(staff.getSalary());
    }
}
```
Output
```
Elon Musk
48
true
[java, python, node, kotlin]
{2019=14000, 2018=12000, 2017=10000}
```
