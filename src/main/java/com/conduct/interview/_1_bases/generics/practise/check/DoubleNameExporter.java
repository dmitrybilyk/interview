package com.conduct.interview._1_bases.generics.practise.check;

public class DoubleNameExporter implements Exporter<Worker, String> {
    @Override
    public String export(Worker worker) {
        return worker.getName() + worker.getName();
    }
}
