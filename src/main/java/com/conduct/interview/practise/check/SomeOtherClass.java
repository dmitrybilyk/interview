package com.conduct.interview.practise.check;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SomeOtherClass {

    @Autowired
    @Qualifier("myName2")
    private SomeClass myName;
}
