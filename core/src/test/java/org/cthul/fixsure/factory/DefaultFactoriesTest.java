package org.cthul.fixsure.factory;

import java.util.List;
import java.util.Objects;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Typed;
import org.cthul.fixsure.fetchers.Fetchers;
import org.cthul.fixsure.fluents.BiDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.cthul.fixsure.generators.primitives.RandomIntegersGenerator.integers;
import static org.cthul.fixsure.generators.value.ItemsSequence.sequence;

/**
 *
 */
public class DefaultFactoriesTest {
    
    private static Factories factories;
    
    public DefaultFactoriesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        DataSource<String> streets = sequence("Apple Street", "Banana Street").repeat();
        DataSource<Integer> numbers = integers(1, 10);
        DataSource<String> cities = sequence("Amsterdam", "Berlin", "Cape Town").repeat();
        DataSource<String> firstNames = sequence("Alice", "Bob", "Carol", "Dave", "Eve").repeat();
        DataSource<String> lastNames = sequence("Doe", "Smith", "Black").repeat();
        factories = new DefaultFactories.Setup()
                .add("number", numbers)
                .add("city", cities)
                .add("firstName", firstNames)
                .add("lastName", lastNames)
                .newFactory("street", String.class)
                        .with(() -> new StringBuilder())
                        .assign("name").to(streets)
                        .set(StringBuilder::append).to(vm -> vm.get("name"))
                        .then(sb -> sb.append(" "))
                        .build(StringBuilder::toString)
                        .apply("number", (str, n) -> str + n)
                .newFactory(Address.class)
                        .set("street")
                        .set("city").toNext("city")
                .newFactory(Person.class)
                        .set("name").to(v -> v.get("firstName") + " " + v.get("lastName"))
                        .set("address").toNext(Address.class)
                .toFactories();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        factories.reset();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_create() {
        Person p = factories.create(Person.class);
        assertThat(p.name, is("Alice Doe"));
        assertThat(p.address.city, is("Amsterdam"));
        assertThat(p.address.street, startsWith("Apple Street "));
        
        p = factories.create(Person.class);
        assertThat(p.name, is("Bob Smith"));
        assertThat(p.address.city, is("Berlin"));
        assertThat(p.address.street, startsWith("Banana Street "));
    }

    @Test
    public void test_create_with_const_arg() {
        Address a = factories.create(Address.class);
        List<Person> people = factories.generate(Person.class, "address", a).two();
        assertThat(people, hasSize(2));
        assertThat(people.get(0).name, is("Alice Doe"));
        assertThat(people.get(0).address, is(a));
        assertThat(people.get(1).name, is("Bob Smith"));
        assertThat(people.get(1).address, is(a));
    }

    @Test
    public void test_create_with_source_arg() {
        DataSource<String> streets = sequence("A", "B").repeat();
        Generator<Address> addresses = factories.generate(Address.class, "street", streets);
        Address a = addresses.next();
        assertThat(a.street, is("A"));
        assertThat(a.city, is("Amsterdam"));
        
        a = addresses.next();
        assertThat(a.street, is("B"));
        assertThat(a.city, is("Berlin"));
        
        a = addresses.next();
        assertThat(a.street, is("A"));
        assertThat(a.city, is("Cape Town"));
    }

    @Test
    public void test_create_with_reference_arg() {
        Generator<Address> addresses = factories.generate(Address.class)
                .set("street").toNext("firstName");
        Address a = addresses.next();
        assertThat(a.street, is("Alice"));
        assertThat(a.city, is("Amsterdam"));
        
        a = addresses.next();
        assertThat(a.street, is("Bob"));
        assertThat(a.city, is("Berlin"));
        
        a = addresses.next();
        assertThat(a.street, is("Carol"));
        assertThat(a.city, is("Cape Town"));
    }
    
    @Test
    public void test_create_overriding_implicit_arg() {
        Person p = factories.create(Person.class, "firstName", "Aligator");
        assertThat(p.name, is("Aligator Doe"));
    }
    
    @Test
    public void test_create_overriding_complex_arg() {
        Generator<String> names = sequence("Alice", "Bob").repeat().newGenerator();
        Factories factories2 = new DefaultFactories.Setup()
                .newFactory(Person.class)
                    .set("name").to(vm -> names.next())
                .toFactories();
        Person p = factories2.create(Person.class, "name", sequence("Carol"));
        assertThat(p.name, is("Carol"));
    }
    
    @Test
    public void test_create_with_nested_source_arg() {
        DataSource<String> streets = sequence("A", "B").repeat();
        Generator<Person> people = factories.generate(Person.class, "address.street", streets);
        Person p = people.next();
        assertThat(p.address.street, is("A"));
        p = people.next();
        assertThat(p.address.street, is("B"));
    }
    
    @Test
    public void test_create_with_nested_reference_arg() {
        Generator<Person> people = factories.generate(Person.class)
                .set("address.street").toNext("firstName");
        Person p = people.next();
        assertThat(p.name, is("Alice Doe"));
        assertThat(p.address.street, is("Bob"));
        p = people.next();
        assertThat(p.name, is("Carol Smith"));
        assertThat(p.address.street, is("Dave"));
    }
    
    @Test
    public void test_create_overriding_nested_complex_arg() {
        Generator<String> streets = sequence("A", "B").repeat().newGenerator();
        Factories factories2 = new DefaultFactories.Setup()
                .newFactory(Address.class)
                    .set("street").to(vm -> streets.next())
                .newFactory(Person.class)
                    .set("address").toNext(Address.class)
                .toFactories();
        Person p = factories2.create(Person.class, "address.street", sequence("C"));
        assertThat(p.address.street, is("C"));
    }
    
    @Test
    public void test_created_nested_twice() {
        Person p = factories.generate(Person.class, "address.street.name", "Foo")
                    .set("address.street.number").toNext(n -> 13 + (int) n)
                    .next();
        assertThat(p.address.street, is("Foo 13"));
    }
    
    @Test
    public void test_reset() {
        Person p = factories.create(Person.class);
        assertThat(p.name, is("Alice Doe"));
        assertThat(p.address.city, is("Amsterdam"));
        assertThat(p.address.street, startsWith("Apple Street "));
        int aNumber = factories.next("number");
        
        factories.reset();
        p = factories.create(Person.class);
        assertThat(p.name, is("Alice Doe"));
        assertThat(p.address.city, is("Amsterdam"));
        assertThat(p.address.street, startsWith("Apple Street "));
        assertThat(factories.next("number"), is(aNumber));
    }
    
    @Test
    public void test_assign_with_builder() {
        Factories factories2 = new DefaultFactories.Setup()
                .newFactory("string", String.class)
                    .with(() -> new StringBuilder())
                    .assign("key1").to(sequence(1, 2, 3))
                    .apply(StringBuilder::append).to(vm -> vm.get("key2") + "-")
                    .build(StringBuilder::toString)
                    .assign("key2").to(sequence(10, 20, 30))
                    .apply((s,o) -> s+o).to(vm -> vm.get("key1"))
                .toFactories();
        assertThat(factories2.next("string"), is("10-1"));
        assertThat(factories2.create("string", "key1", 4), is("20-4"));
        assertThat(factories2.create("string", "key2", 40), is("40-2"));
    }
    
    @Test
    public void test_more() {
        List<Person> people = factories.generate(Person.class)
                .shuffle().distinct().many();
        assertThat(people, hasSize(greaterThan(90)));
        people.forEach(System.out::println);
    }
    
    @Test
    public void test_assign_bisource() {
        BiDataSource<Character, String> firstNames =
                sequence('F', 'M')
                .with(sequence("Alice", "Bob"));
        Factories factories2 = new DefaultFactories.Setup()
                .add("gender,firstName", firstNames.pairs())
                .newFactory(Person.class)
                    .assign("gender", "firstName").toValue("gender,firstName")
                    .assign("title").to(vm -> vm.get("gender").equals('F') ? "Mrs" : "Mr")
                    .set("name").to(vm -> vm.get("title") + " " + vm.get("firstName"))
                .toFactories();
        Person p = factories2.create(Person.class);
        assertThat(p.name, is("Mrs Alice"));
        p = factories2.create(Person.class);
        assertThat(p.name, is("Mr Bob"));
    }
    
    @Test
    public void test_create_list() {
        Typed<List<Person>> PEOPLE = Typed.token("people");
        
        Factories factories2 = new DefaultFactories.Setup()
                .newFactory(Person.class)
                    .set("name").to(sequence("Alice", "Bob", "Carol").repeat())
                    .set("address").toNext(Address.class)
                .newFactory(Address.class)
                    .set("street").to("Street")
                    .set("city").to(sequence("Amsterdam", "Berlin", "Cape Town").repeat())
                .newFactory(PEOPLE)
                    .assign("size").to(Fetchers.some())
                    .build(vm -> vm.generate(Person.class).next(vm.getInt("size")))
                .toFactories();
        List<Person> people = factories2.create(PEOPLE);
        assertThat(people, hasSize(lessThan(9)));
        assertThat(people, hasSize(greaterThan(4)));
        assertThat(people.get(0).name, is("Alice"));
        assertThat(people.get(1).name, is("Bob"));
        assertThat(people.get(2).name, is("Carol"));
        
        people = factories2.create(PEOPLE, "size", 3);
        assertThat(people, hasSize(3));
    }
    
    @Test
    public void test_extend() {
        Factories factories2 = new DefaultFactories.Setup()
                .newFactory(Person.class)
                    .extend(factories.factory(Person.class))
                    .assign("name").to("Zoe")
                .toFactories();
        Person p = factories2.create(Person.class);
        assertThat(p.name, is("Zoe"));
    }

    public static class Address {
        
        public String street;
        public String city;

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.street);
            hash = 89 * hash + Objects.hashCode(this.city);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Address other = (Address) obj;
            if (!Objects.equals(this.street, other.street)) {
                return false;
            }
            if (!Objects.equals(this.city, other.city)) {
                return false;
            }
            return true;
        }
    }
    
    public static class Person {
        
        public String name;
        public Address address;

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(this.name);
            hash = 89 * hash + Objects.hashCode(this.address);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Person other = (Person) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.address, other.address)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return name + "\n" + address.street + "\n" + address.city + "\n";
        }
    }
}
