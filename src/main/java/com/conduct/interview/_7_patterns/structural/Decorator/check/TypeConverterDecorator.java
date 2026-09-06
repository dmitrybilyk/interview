package com.conduct.interview._7_patterns.structural.decorator.check;

public class TypeConverterDecorator implements TypeConverter {
    private final TypeConverter converter;

    public TypeConverterDecorator(TypeConverter converter) {
        this.converter = converter;
    }

    @Override
    public String convert(Integer value) {
        String converted = converter.convert(value);
        return String.format("## %s ##", converted);
    }
}
