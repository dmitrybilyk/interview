package com.conduct.interview._1_bases.generics;

import java.util.ArrayList;

public class Combined {
    static class Fruit {
        int weight;
    }
    static class Citrus extends Fruit {
        int weight;
    }
    static class Orange extends Citrus {
        int color;
    }
    static class BigRoundOrange extends Orange {
        int size = 100;
    }

    public static void main(String[] args) {
        Fruit fruit = new Fruit();
        Citrus citrus = new Citrus();
        Orange orange = new Orange();

//        We can assign orange to citrus because it's lower in the inheritance hierarchi
        citrus = orange;

//        orange = fruit  <<< compilation error

        ArrayList<Citrus> citruses = new ArrayList<>();
        fruit = citruses.get(0);
        citrus = citruses.get(0);

//        orange = citruses.get(0); <<< compilation error, need to cast

        ArrayList<Orange> oranges = new ArrayList<>();
        Citrus citrus1 = oranges.get(0);
//        this works well because we have parent on the left side
//        citruses = oranges;
//        oranges = citruses;
//        <<< compilation error. Neither of assignments work.
//        Because it's forbidden. In case it worked we would
//        get from source list not orange but citrus (different type, has different set of fields even).
//        This is called invariance. Generics in java are invariant.

        totalWeightInitialOranges(oranges); // <<< works well
//        totalWeightInitialOranges(citruses);
//                <<< compilation error because of invariance
        totalWeightWithCovarianceCitruses(citruses);
        // works after adding `? extends Citrus`. // It's called Covariance!!!.
//      With a such covariant type we can read objects of Citrus and all his parents
//        We can not add anything to the list of such a type because we can not predict
//        and know how many children types Citrus has and we can break an integrity of the list
//        (different types in the same list)

//        So in case of covariant type we can send to `totalWeight` method list of mentioned class and
//        also lists of all his children. To read from this covariant list we may class Citrus and all his
//        parents. And we can not add to that list at all!


//        ----------- adding to list ------------
        addToListInitialOranges(oranges); // works well
//        addToListInitialOranges(citruses); // compilation error because types are invariant.
//        But it would be ok to add orange ot the list of citruses as orange is a child
//        of citrus and would not break anything:
//        citruses.add(new Orange()); Works well
//

//        after made type Contravariant - `? super Citrus` - method accepts Citruses:
        addToListWithContravarianceOranges(citruses); // works well
        addToListWithContravarianceOranges(new ArrayList<Fruit>()); // works well
//        addToListWithContravarianceOranges(new ArrayList<BigRoundOrange>()); // not ok, can't send list of children
//
//        From Contravariant type we can get just Object as it's a Parent of anything. It's impossible
//        to determine which parent you want and java returns just Object.
//        We can send to such a methods just List of edge type or it's Parents (because adding the child would
//        add new fields....
//        To such a list we can add just edge type or it's children


    }

    private static int totalWeightInitialOranges(ArrayList<Orange> oranges) {
        int weight = 0;
        for(int i = 0; i < oranges.size(); i++) {
            weight += oranges.get(i).weight;
        }
        return weight;
    }

    //after adding of `? extends Citrus` we can calculate weight for Citrus and all his children
    private static int totalWeightWithCovarianceCitruses(ArrayList<? extends Citrus> citruses) {
//  so we can read with covariance from citruses:
        Citrus citrus = citruses.get(0);  // <<< we can read the same type
        Fruit fruit = citruses.get(0);  // <<< we can read the same type
//  but we can't read of course child objects because on the left side we should have the same or bigger:
//        Orange orange = citruses.get(0);  // <<< we can't read child objects


//  We can't add to covariant list at all:
//        citruses.add(new Citrus());  <<< can't add
//        citruses.add(new Fruit());   <<< can't add
//        citruses.add(new Orange());  <<< can't add
//        citruses.add(new Object());  <<< can't add

        int weight = 0;
        for(int i = 0; i < citruses.size(); i++) {
            weight += citruses.get(i).weight;
        }
        return weight;
    }

//initial. Works just for Oranges
    private static void addToListInitialOranges(ArrayList<Orange> oranges) {
        for(int i = 0; i < oranges.size(); i++) {
            oranges.add(new Orange());
        }
    }


    //second. changed type to `? super Orange` - and now it's a Contravariant type
    private static void addToListWithContravarianceOranges(ArrayList<? super Orange> oranges) {

//        What we can get - just object:
//        Orange orange = oranges.get(0); //can't as it's impossible for java to understand which type
//        Citrus orange = oranges.get(0); //can't
          Object orange = oranges.get(0); // just this we can

//        What we can add - just the same or child:
        oranges.add(new Orange());
        oranges.add(new BigRoundOrange());
//        oranges.add(new Citrus());


        for(int i = 0; i < oranges.size(); i++) {
            oranges.add(new Orange());
        }
    }
}

