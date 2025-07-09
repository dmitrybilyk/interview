package com.conduct.interview.practise.spring.transactions.service;

import com.conduct.interview.practise.spring.transactions.MyEntity;
import com.conduct.interview.practise.spring.transactions.repository.MyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("postgres")
public class AnotherService {

    @Autowired
    private MyRepository repository;

//    @Transactional
//    @Transactional(propagation = Propagation.NEVER)
//    @Transactional(propagation = Propagation.NESTED) // can execute in scope
//    of existing transaction, but uses it's own savepoints
//    @Transactional(propagation = Propagation.MANDATORY)
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void anotherServiceMethod() {
        MyEntity entity = new MyEntity();
        entity.setName("Entity in from inner");
        repository.save(entity);
//        throw new RuntimeException("with requires_new failed");
    }

//    @Transactional(propagation = Propagation.NESTED)
    public void anotherServiceMethodForNested(MyEntity myEntity) {
        repository.save(myEntity);
        if (myEntity.getName().equals("Nested2")) {
            throw new RuntimeException("Nested2 failed");
        }
    }
}
