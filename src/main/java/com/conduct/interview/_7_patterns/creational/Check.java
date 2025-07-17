package com.conduct.interview._7_patterns.creational;

public class Check {
    public static void main(String[] args) {
        EnumSingleton.HANDLER.doSomething();

        BestSingleton bestSingleton = BestSingleton.getInstance();
        BestSingleton bestSingleton2 = BestSingleton.getInstance();

        User user = new User.Builder(5).setAge(4).build();
        System.out.println(user.toString());

        AnotherUser anotherUser = AnotherUser.builder("some name").address("addr").age(20).build();

        ProductFactory productFactory = new GoodProductFactory();
        Product product = productFactory.createProduct();
        System.out.println(product.toString());

        ProductFactoryProvider productFactoryProvider = ProductFactoryProvider.getInstance();
        ProductFactory productFactory1 = productFactoryProvider.getProductFactory();
        Product product1 = productFactory1.createProduct();
        System.out.println("Name - " + product1.getName() + ", type - " + product1.getType());
    }
}

class ProductFactoryProvider {
    private final ProductFactory productFactory;

    private ProductFactoryProvider() {
        boolean badProductEnabled = Boolean.getBoolean("bad.product.enabled");
        if (badProductEnabled) {
            productFactory = new BadProductFactory();
        } else {
            productFactory = new GoodProductFactory();
        }
    }

    public static ProductFactoryProvider getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ProductFactory getProductFactory() {
        return productFactory;
    }

    private static class SingletonHolder {
        private final static ProductFactoryProvider INSTANCE = new ProductFactoryProvider();
    }
}

class Customer {
    private String name;
    private Customer(String name) {
        this.name = name;
    }

    public static Customer createCustomer(String name) {
        return new Customer(name);
    }
}

class Product {
    enum ProductType {
        GOOD, BAD
    }
    private String name;
    private ProductType type;

    public Product(String name, ProductType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

}

class GoodProduct extends Product {
    public GoodProduct(String name, ProductType type) {
        super(name, type);
    }
}

class BadProduct extends Product {
    public BadProduct(String name, ProductType type) {
        super(name, type);
    }
}



interface ProductFactory {
    Product createProduct();
}

class GoodProductFactory implements ProductFactory {

    @Override
    public Product createProduct() {
        return new GoodProduct("Good Product", Product.ProductType.GOOD);
    }
}

class BadProductFactory implements ProductFactory {

    @Override
    public Product createProduct() {
        return new BadProduct("Bad Product", Product.ProductType.BAD);
    }
}


final class SimpleFactory {
    private SimpleFactory() {
    }

    public static Product create(Product.ProductType type) {
        return switch (type) {
            case GOOD -> new GoodProduct("Bad Product", Product.ProductType.GOOD);
            case BAD -> new BadProduct("Bad Product", Product.ProductType.BAD);
            default -> throw new IllegalArgumentException("Unknown product type: " + type);
        };
    }
}


class MySingleton {
    private static volatile MySingleton instance;

    private MySingleton() {}

    public static MySingleton getInstance() {
        synchronized (MySingleton.class) {
            if (instance == null) {
                synchronized (MySingleton.class) {
                    if (instance == null) {
                        instance = new MySingleton();
                    }
                }
            }
        }

        return instance;
    }
}

enum EnumSingleton {
    HANDLER;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void doSomething() {
        System.out.println("Hello " + name);
    }
}

class BestSingleton {
    private BestSingleton() {}

    private static class SingletonHolder {
        private static final BestSingleton INSTANCE = new BestSingleton();
    }

    public static BestSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}


class User {
    private final int id;
    private final String name;
    private final int age;

    public User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.age = builder.age;
    }

    Builder builder(int id) {
        return new Builder(id);
    }

    public static class Builder {
        private int id;
        private String name;
        private int age;

        public Builder(int id) {
            this.id = id;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}





class AnotherUser {
    private final String name;
    private final String address;
    private final int age;

    private AnotherUser(UserBuilder userBuilder) {
        this.name = userBuilder.name;
        this.address = userBuilder.address;
        this.age = userBuilder.age;
    }

    public static UserBuilder builder(String name) {
        return new UserBuilder(name);
    }

    static class UserBuilder {
        private String name;
        private String address;
        private int age;

        UserBuilder(String name) {
            this.name = name;
        }

        UserBuilder address(String address) {
            this.address = address;
            return this;
        }

        UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        public AnotherUser build() {
            return new AnotherUser(this);
        }

    }
}
