package org.design.designurlshortenergenerator.persistence.model.sql;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    private String message;

    public Event(String message) {
        this.message = message;
    }

    protected Event() {}
}