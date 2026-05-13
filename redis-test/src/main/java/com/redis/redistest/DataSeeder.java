//package com.redis.redistest;
//
//import com.redis.redistest.cases.cache_aside_with_string.ProductRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//@Configuration
//@Profile("redis-add-data")
//public class DataSeeder {
//
//    @Bean
//    CommandLineRunner initDatabase(ProductRepository repository) {
//        return args -> {
//            if (repository.count() == 0) {
//                System.out.println("Seeding 10,000 products...");
//                List<Product> products = new ArrayList<>();
//                Random random = new Random();
//
//                for (int i = 1; i <= 10000; i++) {
//                    Product p = new Product();
//                    p.setName("Product-" + i);
//                    p.setPrice(10 + (500 - 10) * random.nextDouble());
//                    p.setStock(random.nextInt(100));
//                    products.add(p);
//                }
//                repository.saveAll(products);
//                System.out.println("Seeding complete.");
//            }
//        };
//    }
//}