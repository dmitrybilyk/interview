package com.conduct.interview._1_bases.generics.practise.check;

public class CompanyUtils {
    public static <T extends Worker> void copyWorkers(
            Company<? extends T, ?> source,
            Company<? super T, ?> target) {
        for (T worker : source.getWorkers()) {
            target.addWorker(worker);
        }
    }
}
