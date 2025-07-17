package com.conduct.interview._1_bases.generics.practise.check;

@FunctionalInterface
public interface Exporter<T, R> {
    R export(T t);
}
