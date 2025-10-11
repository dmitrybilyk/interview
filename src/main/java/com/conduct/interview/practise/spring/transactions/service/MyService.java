//package com.conduct.interview.practise.spring.transactions.service;
//
//import com.conduct.interview.practise.spring.transactions.MyEntity;
//import com.conduct.interview.practise.spring.transactions.repository.MyRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Profile("postgres")
//public class MyService {
//
//    @Autowired
//    private MyRepository repository;
//
//    @Autowired
//    private AnotherService anotherService;
//
//    @Transactional
//    public void outerTransaction() {
//        MyEntity entity1 = new MyEntity();
//        entity1.setName("Entity 1");
//        repository.save(entity1);
//
//        // Calling another service which requires a new transaction
//        anotherService.anotherServiceMethod();
////        anotherService.anotherServiceMethodForNested(new MyEntity(null, "Nested1"));
////        anotherService.anotherServiceMethodForNested(new MyEntity(null, "Nested2"));
////        anotherService.anotherServiceMethodForNested(new MyEntity(null, "Nested3"));
//
//        MyEntity entity2 = new MyEntity();
//        entity2.setName("Entity 2");
//        repository.save(entity2);
//
//        // Throwing an exception to test rollback behavior
////        if (true) {
////            throw new RuntimeException("Simulating an error");
////        }
//    }
//}
