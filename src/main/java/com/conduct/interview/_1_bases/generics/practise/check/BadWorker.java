package com.conduct.interview._1_bases.generics.practise.check;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadWorker extends Worker {
    public BadWorker(String name, String profession) {
        super(name, profession);
    }
}
