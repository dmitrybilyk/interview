package com.conduct.interview._7_patterns.structural.decorator.check;

public class TypeConverterImpl implements TypeConverter {
    @Override
    public String convert(Integer value) {
        return value.toString();
    }
}
