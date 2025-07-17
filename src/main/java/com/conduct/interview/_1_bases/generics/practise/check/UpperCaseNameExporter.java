package com.conduct.interview._1_bases.generics.practise.check;

public class UpperCaseNameExporter implements Exporter<Worker, String> {
    @Override
    public String export(Worker worker) {
        return worker.getName().toUpperCase();
    }
}
