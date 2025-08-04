package com.conduct.interview._7_patterns.behavioral.templatemethod.buildhouse;

/**
 * Building particular type of house depending on env property
 */
public class HouseBuilderFactoryImpl implements HouseBuilderFactory {
    private static final HouseBuilderFactory INSTANCE = new HouseBuilderFactoryImpl();

    public static HouseBuilderFactory getInstance() {
        return INSTANCE;
    }

    private HouseBuilderFactoryImpl(){}

    @Override
    public HouseBuilder buildHouse() {
        String houseType = System.getProperty("house.type");
        if (houseType == null) {
            throw new IllegalArgumentException("Unable to build house. Property 'house.type' is not found");
        }

        return switch (houseType) {
            case "cheap" -> new CheapHouseBuilder();
            case "luxury" -> new LuxuryHouseBuilder();
            default -> throw new IllegalArgumentException("Unable to build house. Unknown house type: " + houseType);
        };
    }
}
