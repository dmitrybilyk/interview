package com.conduct.interview._3_spring._3_bean_scopes;

import java.util.UUID;

public class PrototypeBean {
    private final String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }
}
