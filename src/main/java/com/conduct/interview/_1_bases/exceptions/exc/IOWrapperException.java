package com.conduct.interview._1_bases.exceptions.exc;

public class IOWrapperException extends Exception {
    public IOWrapperException(String message) {
        super(message);
    }
    public IOWrapperException(String message, Throwable ex) {
        super(message, ex);
    }
}
