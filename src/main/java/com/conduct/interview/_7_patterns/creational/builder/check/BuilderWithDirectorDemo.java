package com.conduct.interview._7_patterns.creational.builder.check;// ================================================
//  Builder Pattern with Director - Single File Example
// ================================================

public class BuilderWithDirectorDemo {

    public static void main(String[] args) {

        // Director
        ConstructionDirector director = new ConstructionDirector();

        // Using Wooden House Builder
        HouseBuilder woodenBuilder = new WoodenHouseBuilder();
        House woodenHouse = director.constructFullHouse(woodenBuilder);
        System.out.println("Wooden House Built:");
        System.out.println(woodenHouse);

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Using Concrete (Modern) House Builder
        HouseBuilder concreteBuilder = new ConcreteHouseBuilder();
        House modernHouse = director.constructFullHouse(concreteBuilder);
        System.out.println("Modern Concrete House Built:");
        System.out.println(modernHouse);

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Using Director for Basic House (different construction process)
        House basicWoodenHouse = director.constructBasicHouse(woodenBuilder);
        System.out.println("Basic Wooden House (no painting & furnishing):");
        System.out.println(basicWoodenHouse);
    }
}

// ==================== Product ====================
class House {
    private String foundation;
    private String structure;
    private String roof;
    private String painting;
    private String furnishing;

    // Setters (used by builders)
    public void setFoundation(String foundation) { this.foundation = foundation; }
    public void setStructure(String structure)   { this.structure = structure; }
    public void setRoof(String roof)             { this.roof = roof; }
    public void setPainting(String painting)     { this.painting = painting; }
    public void setFurnishing(String furnishing) { this.furnishing = furnishing; }

    @Override
    public String toString() {
        return "House {\n" +
                "  Foundation   = " + foundation + "\n" +
                "  Structure    = " + structure + "\n" +
                "  Roof         = " + roof + "\n" +
                "  Painting     = " + painting + "\n" +
                "  Furnishing   = " + furnishing + "\n" +
                "}";
    }
}

// ==================== Builder Interface ====================
interface HouseBuilder {
    void buildFoundation();
    void buildStructure();
    void buildRoof();
    void paint();
    void furnish();
    House getResult();
}

// ==================== Concrete Builder 1 ====================
class WoodenHouseBuilder implements HouseBuilder {

    private final House house = new House();

    @Override
    public void buildFoundation() {
        house.setFoundation("Wooden Piles + Stone Base");
    }

    @Override
    public void buildStructure() {
        house.setStructure("Wooden Beams and Logs");
    }

    @Override
    public void buildRoof() {
        house.setRoof("Sloped Wooden Roof with Tiles");
    }

    @Override
    public void paint() {
        house.setPainting("Natural Wood Protection Varnish");
    }

    @Override
    public void furnish() {
        house.setFurnishing("Rustic Wooden Furniture");
    }

    @Override
    public House getResult() {
        return house;
    }
}

// ==================== Concrete Builder 2 ====================
class ConcreteHouseBuilder implements HouseBuilder {

    private final House house = new House();

    @Override
    public void buildFoundation() {
        house.setFoundation("Reinforced Concrete Foundation");
    }

    @Override
    public void buildStructure() {
        house.setStructure("Concrete Walls + Steel Frame");
    }

    @Override
    public void buildRoof() {
        house.setRoof("Flat Concrete Roof with Waterproofing");
    }

    @Override
    public void paint() {
        house.setPainting("Modern White + Gray Exterior Paint");
    }

    @Override
    public void furnish() {
        house.setFurnishing("Modern Minimalist Furniture");
    }

    @Override
    public House getResult() {
        return house;
    }
}

// ==================== Director ====================
class ConstructionDirector {

    // Full luxury house construction process
    public House constructFullHouse(HouseBuilder builder) {
        builder.buildFoundation();
        builder.buildStructure();
        builder.buildRoof();
        builder.paint();
        builder.furnish();
        return builder.getResult();
    }

    // Basic house - different process (skips painting and furnishing)
    public House constructBasicHouse(HouseBuilder builder) {
        builder.buildFoundation();
        builder.buildStructure();
        builder.buildRoof();
        return builder.getResult();
    }
}