package com.conduct.interview._1_bases.generics.practise.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Company<T extends Worker, R> {
    private final List<T> workers = new ArrayList<>();

    public void addWorker(T worker) {
        workers.add(worker);
    }

    public List<T> getWorkers() {
        return new ArrayList<>(workers);
    }

    public List<T> findWorkers(Predicate<? super T> predicate) {
        List<T> result = new ArrayList<>();
        for (T worker : workers) {
            if (predicate.test(worker)) {
                result.add(worker);
            }
        }
        return result;
    }

    public List<R> mapWorkers(Function<? super T, ? extends R> mapper) {
        List<R> result = new ArrayList<>();
        for (T worker : workers) {
            result.add(mapper.apply(worker));
        }
        return result;
    }

    public List<R> exportWorkers(Exporter<? super T, ? extends R> exporter) {
        List<R> result = new ArrayList<>();
        for (T worker : workers) {
            result.add(exporter.export(worker));
        }
        return result;
    }

    public void sortWorkers(Comparator<? super T> comparator) {
        workers.sort(comparator);
    }
}
