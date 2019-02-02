package org.cthul.fixsure;

import java.util.List;
import org.cthul.fixsure.data.English;
import org.cthul.fixsure.data.StringFormatter;
import org.cthul.fixsure.factory.Factories;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 */
public class IntegrationTest {
    
    public static class Person {
        private String name;
        private int age;
        private Address address;

        public int getAge() {
            return age;
        }
        
        @Override
        public String toString() {
            return name + ", " + address;
        }
    }
    
    public static class Address {
        private String street;
        private String city;
        
        @Override
        public String toString() {
            return street + ", " + city;
        }
    }
    
    Factories factories = Factories.build()
            .newFactory(Person.class)
                .assign("firstName", "gender", English.aliceBobWithGender().repeat())
                .assign("title").to(v -> v.c("gender") == 'F' ? "Mrs" : "Mr")
                .assign("lastName").to(English.lastNames())
                .set("name").to(StringFormatter.formatString("%:title$s %:firstName$s %:lastName$s"))
                .set("age").to(Fixsure.integers(18, 100))
                .set("address")
            .newFactory("street+nr")
                .with(StringBuilder::new)
                .apply("name", StringBuilder::append).to(English.fruits().map(f -> f + " Street"))
                .then(sb -> sb.append(' '))
                .apply("number", StringBuilder::append).to(Fixsure.integers(1, 10))
                .build(StringBuilder::toString)
            .newFactory(Address.class)
                .set("street").toNext("street+nr")
                .set("city").to(Fixsure.sequence("Amsterdam", "Berlin", "Cape Town").random())
            .toFactories();
    
    @Test
    public void test() {
        Person p = factories.create(Person.class, "lastName", "Doe", "address.street.number", 13);
        assertEquals("Mrs Alice Doe, Apple Street 13, Berlin", p.toString());
        
        List<Person> people = factories.generate(Person.class, "age", Fixsure.consecutiveIntegers()).several();
        assertTrue(people.size() >= 8);
        for (int i = 0; i < people.size(); i++) {
            assertEquals(i, people.get(i).getAge());
        }
    }
    
    @Test
    public void test_pseudorandomness() {
        int firstInt = Fixsure.integers(0, 100).first();
        String idString = String.format("Mrs Alice Smith, Apple Street %d, Berlin",firstInt);
        Person p = factories.create(Person.class, "address.street.number", Fixsure.integers(0, 100));
        assertEquals(idString, p.toString());
    }
}
