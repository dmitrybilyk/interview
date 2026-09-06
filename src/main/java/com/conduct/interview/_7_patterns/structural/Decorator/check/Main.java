package com.conduct.interview._7_patterns.structural.decorator.check;

public class Main {
    public static void main(String[] args) {
        TypeConverter typeConverter = new TypeConverterImpl();
        TypeConverter typeConverterDecorator = new TypeConverterDecorator(typeConverter);
        String result = typeConverterDecorator.convert(44);
        System.out.println(result);
    }
}
