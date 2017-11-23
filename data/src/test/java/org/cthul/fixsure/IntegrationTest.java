package org.cthul.fixsure;

import java.util.List;
import org.cthul.fixsure.data.English;
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
                .assign("firstName").to(English.aliceBob())
                .assign("lastName").to(English.lastNames())
                .set("name").to(v -> v.getStr("firstName") + " " + v.getStr("lastName"))
                .set("age").to(Fixsure.integers(18, 100))
                .set("address").toNext(Address.class)
            .newFactory("street+nr")
                .with(StringBuilder::new)
                .apply("name", StringBuilder::append).to(English.fruitsA2Z().map(f -> f + " Street"))
                .apply(StringBuilder::append).to(" ")
                .apply("number", StringBuilder::append).to(Fixsure.integers(1, 10))
                .build(StringBuilder::toString)
            .newFactory(Address.class)
                .set("street").toNext("street+nr")
                .set("city").to(Fixsure.sequence("Amsterdam", "Berlin", "Cape Town").random())
            .toFactories();
    
    @Test
    public void test() {
        Person p = factories.create(Person.class, "lastName", "Doe", "address.street.number", 13);
        assertEquals("Alice Doe, Apple Street 13, Berlin", p.toString());
        
        List<Person> people = factories.generate(Person.class, "age", Fixsure.integers().ordered()).several();
        assertTrue(people.size() >= 8);
        for (int i = 0; i < people.size(); i++) {
            assertEquals(i, people.get(i).getAge());
        }
    }
    
    @Test
    public void test_pseudorandom1() {
        Person p = factories.create(Person.class, "lastName", "Doe", "address.street.number", 13);
        assertEquals("Alice Doe, Apple Street 13, Berlin", p.toString());
    }
    
    @Test
    public void test_pseudorandom2() {
        Person p = factories.create(Person.class, "lastName", "Doe", "address.street.number", 13);
        assertEquals("Alice Doe, Apple Street 13, Berlin", p.toString());
    }
    
    @Test
    public void test_pseudorandom3() {
        Person p = factories.create(Person.class, "lastName", "Doe", "address.street.number", 13);
        assertEquals("Alice Doe, Apple Street 13, Berlin", p.toString());
    }
}
