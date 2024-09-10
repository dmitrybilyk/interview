package com.conduct.interview._7_patterns.creational.singleton.issues.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SingletonTester {
   public static void main(String[] args) {
      
      try {
          SingletonClassTest instance1 = SingletonClassTest.getInstance();
            ObjectOutput out = null;

            out = new ObjectOutputStream(new FileOutputStream("filename.ser"));
            out.writeObject(instance1);
            out.close();

            //deserialize from file to object
            ObjectInput in = new ObjectInputStream(new FileInputStream("filename.ser"));
          SingletonClassTest instance2 = (SingletonClassTest) in.readObject();
            in.close();

            System.out.println("instance1 hashCode=" + instance1.hashCode());
            System.out.println("instance2 hashCode=" + instance2.hashCode());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
   }
}

class SingletonClassTest implements Serializable {

    private static volatile SingletonClassTest sSoleInstance;

    //private constructor.
    private SingletonClassTest() {

        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SingletonClassTest getInstance() {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time

            synchronized (SingletonClassTest.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null)
                    sSoleInstance = new SingletonClassTest();
            }
        }

        return sSoleInstance;
    }

    //SOLUTION >>> Make singleton from serialize and deserialize operation.
    protected Object readResolve() {
        return getInstance();
    }
}