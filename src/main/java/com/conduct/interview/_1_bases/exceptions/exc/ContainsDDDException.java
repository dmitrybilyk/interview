package com.conduct.interview._1_bases.exceptions.exc;

public class ContainsDDDException extends Exception {
    public ContainsDDDException(String message) {
        super(message);
    }
    public ContainsDDDException(String message, Throwable ex) {
        super(message, ex);
    }
}
