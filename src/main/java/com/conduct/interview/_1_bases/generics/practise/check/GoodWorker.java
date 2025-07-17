package com.conduct.interview._1_bases.generics.practise.check;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodWorker extends Worker {
    public GoodWorker(String name, String profession) {
        super(name, profession);
    }
}
