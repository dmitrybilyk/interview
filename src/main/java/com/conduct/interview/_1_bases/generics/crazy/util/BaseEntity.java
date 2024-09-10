package com.conduct.interview._1_bases.generics.crazy.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    protected UUID uuid;
    protected LocalDateTime createdOn;

    public BaseEntity(UUID uuid) {
        this.uuid = uuid;
        this.createdOn = LocalDateTime.now();
    }
}
