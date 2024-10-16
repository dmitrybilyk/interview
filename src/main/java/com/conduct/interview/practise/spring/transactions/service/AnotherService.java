package com.conduct.interview.practise.spring.transactions.service;

import com.conduct.interview.practise.spring.transactions.MyEntity;
import com.conduct.interview.practise.spring.transactions.repository.MyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnotherService {

    @Autowired
    private MyRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerTransaction() {
        MyEntity entity = new MyEntity();
        entity.setName("Entity in new transaction");
        repository.save(entity);
    }
}
