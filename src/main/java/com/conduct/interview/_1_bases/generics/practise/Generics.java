package com.conduct.interview._1_bases.generics.practise;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generics {

    public static void main(String[] args) {
        List<Person> people = List.of(new Person("Alice", 30), new Person("Bob", 25));
        ProcessorRegistry registry = new ProcessorRegistry();
        registry.register(Person.class, new PersonListProcessor());

        AbstractProcessor<Person, List<Person>> personProcessor = registry.get(Person.class);

        personProcessor.process(people);
    }

}

class ProcessorRegistry {
    private final Map<Class<?>, AbstractProcessor<?, ?>> processors = new HashMap<>();

    public <T, C extends Collection<T>> void register(Class<T> type, AbstractProcessor<T, C> processor) {
        processors.put(type, processor);
    }

    @SuppressWarnings("unchecked")
    public <T, C extends Collection<T>> AbstractProcessor <T, C> get(Class<?> type) {
        return (AbstractProcessor<T, C>) processors.get(type);
    }
}


abstract class AbstractProcessor<T, C extends Collection<T>> {
    protected void process(C collection) {
        System.out.println("Some additional logic");
        processCollection(collection);
    }

    protected abstract void processCollection(C collection);
}

class PersonListProcessor extends AbstractProcessor<Person, List<Person>> {
    @Override
    protected void processCollection(List<Person> people) {
        for (Person p : people) {
            System.out.println("Person: " + p.getName() + ", Age: " + p.getAge());
        }
    }
}


class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) { this.name = name; this.age = age; }

    public String getName() { return name; }
    public int getAge() { return age; }
}

class Address {
    private final String city;
    private final String zip;

    public Address(String city, String zip) { this.city = city; this.zip = zip; }

    public String getCity() { return city; }
    public String getZip() { return zip; }
}
