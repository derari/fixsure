# Fixsure

Fixsure is a library for the easy creation of object factories and generation of test data.

### Features

* Generators for primitive values with stream-like fluent DSL
* DSL for defining complex factories
* Pseudo-randomness for repeatable tests

### Code Example

```Java
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
```
