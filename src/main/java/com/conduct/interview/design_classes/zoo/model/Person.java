//package com.conduct.interview.design_classes.zoo.model;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Function;
//
//interface Identifiable<T> {
//    T getId();
//}
//
//class InvalidPersonException extends RuntimeException {
//    public InvalidPersonException(String message) {
//        super(message);
//    }
//}
//
//class ImmutablePerson implements Identifiable<String> {
//    private final String name;
//    private final int age;
//    private final String email;
//    private final List<String> hobbies;
//
//    public ImmutablePerson(String name, int age, String email, List<String> hobbies) {
//        Person personToValidate = new Person();
//        personToValidate.setName(name);
//        personToValidate.setAge(age);
//        personToValidate.setEmail(email);
//        hobbies.forEach(personToValidate::addHobby);
//
//        List<String> errors = Person.PersonValidator.validatePerson(personToValidate);
//        if (errors.isEmpty()) {
//            this.name = name;
//            this.age = age;
//            this.email = email;
//            this.hobbies = List.copyOf(hobbies);
//        } else {
//            throw new InvalidPersonException("Invalid person data: " + String.join(", ", errors));
//        }
//    }
//
//    public ImmutablePerson(Builder builder) {
//        this.name = builder.name;
//        this.age = builder.age;
//        this.email = builder.email;
//        this.hobbies = builder.hobbies;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public List<String> getHobbies() {
//        return List.copyOf(hobbies);
//    }
//
//    public static class Builder {
//        private String name;
//        private int age;
//        private String email;
//        private List<String> hobbies = new ArrayList<>();
//
//        public Builder name(String name){
//            this.name = name;
//            return this;
//        }
//
//        public Builder email(String email){
//            this.email = email;
//            return this;
//        }
//
//        public Builder age(int age){
//            this.age = age;
//            return this;
//        }
//
//        public Builder hobbies(List<String> hobbies){
//            if (hobbies != null) {
//                this.hobbies = new ArrayList<>(hobbies);
//            }
//            return this;
//        }
//
//        public Builder addHobby(String hobby) {
//            if (hobby != null && !hobby.isBlank()) {
//                this.hobbies.add(hobby);
//            }
//            return this;
//        }
//
//        public ImmutablePerson build() {
//            return new ImmutablePerson(name, age, email, hobbies);
//        }
//    }
//
//
//    @Override
//    public String getId() {
//        return this.email;
//    }
//
//    @Override
//    public String toString() {
//        return "ImmutablePerson{" +
//                "name='" + name + '\'' +
//                ", age=" + age +
//                ", email='" + email + '\'' +
//                ", hobbies=" + hobbies +
//                '}';
//    }
//}
//
//public class Person implements Identifiable<String> {
//    private String name;
//    private int age;
//    private String email;
//    private List<String> hobbies;
//
//    static class PersonValidator {
//        public static boolean isValidPerson(Person person) {
//            return validatePerson(person).isEmpty();
//        }
//
//        public static List<String> validateName(String name) {
//            List<String> errors = new ArrayList<>();
//            if (name == null || name.isBlank()) {
//                errors.add("Name field is invalid");
//            }
//            return errors;
//        }
//
//        public static List<String> validateAge(int age) {
//            List<String> errors = new ArrayList<>();
//            if (age < 0) {
//                errors.add("Age field is invalid");
//            }
//            return errors;
//        }
//
//        public static List<String> validateEmail(String email) {
//            List<String> errors = new ArrayList<>();
//            if (email == null || email.isBlank() || !email.contains("@")) {
//                errors.add("Email.kt field is invalid");
//            }
//            return errors;
//        }
//
//        public static List<String> validatePerson(Person person) {
//            List<String> errors = new ArrayList<>();
//            errors.addAll(validateName(person.name));
//            errors.addAll(validateAge(person.age));
//            errors.addAll(validateEmail(person.email));
//            return errors;
//        }
//    }
//
//    public Person() {
//        name = "";
//        age = 0;
//        email = "";
//        hobbies = new ArrayList<>();
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        List<String> errors = PersonValidator.validateName(name);
//        if (!errors.isEmpty()) {
//            throw new InvalidPersonException(String.join("", errors));
//        }
//        this.name = name;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        List<String> errors = PersonValidator.validateAge(age);
//        if (!errors.isEmpty()) {
//            throw new InvalidPersonException(String.join("", errors));
//        }
//        this.age = age;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email == null || !email.contains("@")? "": email;
//    }
//
//    public void addHobby(String hobby) {
//        if (hobby != null && !hobbies.stream()
//                .anyMatch(h -> h.equalsIgnoreCase(hobby))) {
//            this.hobbies.add(hobby);
//        }
//    }
//
//    public List<String> getHobbies() {
//        return this.hobbies;
//    }
//
//    public void removeHobby(String hobby) {
//        hobbies.removeIf(h -> h.equalsIgnoreCase(hobby));
//    }
//
//    public boolean hasHobby(String hobby) {
//        return hobby != null && hobbies.stream()
//                .anyMatch(h -> h.equalsIgnoreCase(hobby));
//    }
//
//    public ImmutablePerson toImmutable() {
//        return new ImmutablePerson(name, age, email, hobbies);
//    }
//
//    public <T> boolean updateId(T newId, Function<T, String> idConverter) {
//        String convertedId = idConverter.apply(newId);
//        if (convertedId != null && convertedId.contains("@")) {
//            this.email = convertedId;
//            return true;
//        }
//        return false;
//    }
//
//    public void validateAndUpdate(Person other) {
//        List<String> errors = PersonValidator.validatePerson(other);
//        if (errors.isEmpty()) {
//            setName(other.name);
//            setAge(other.age);
//            setEmail(other.email);
//        } else {
//            throw new InvalidPersonException("Invalid person data: " + String.join(", ", errors));
//        }
//    }
//
//    @Override
//    public String getId() {
//        return email;
//    }
//
//    @Override
//    public String toString() {
//        return "Person{" +
//                "name='" + name + '\'' +
//                ", age=" + age +
//                ", email='" + email + '\'' +
//                ", hobbies=" + hobbies +
//                '}';
//    }
//
//    public static void main(String[] args) {
//        Identifiable<String> person = PersonFactory.create(PersonType.MUTABLE, "some name", "dfdf@fdf.com");
//    }
//
//}
//
//interface Repository<T extends Identifiable<ID>, ID> {
//    void add(T entity);
//    Optional<T> findById(ID id);
//    List<T> listAll();
//}
//
//class PersonRepositoryImpl implements Repository<ImmutablePerson, String> {
//
//    private Map<String, ImmutablePerson> personMap = new ConcurrentHashMap<>();
//
//    @Override
//    public void add(ImmutablePerson person) {
//        personMap.put(person.getId(), person);
//    }
//
//    @Override
//    public Optional<ImmutablePerson> findById(String email) {
//        return Optional.ofNullable(personMap.get(email));
//    }
//
//    @Override
//    public List<ImmutablePerson> listAll() {
//        return List.copyOf(personMap.values());
//    }
//}
//
//class PersonFactory {
//
//        public static Identifiable create(PersonType type, String name, String email) {
//            return switch (type) {
//                case MUTABLE -> new Person();
//                case IMMUTABLE -> new ImmutablePerson.Builder()
//                        .name(name)
//                        .email(email)
//                        .build();
//            };
//        }
//}
//
//enum PersonType {
//    MUTABLE,
//    IMMUTABLE
//}
//
//
