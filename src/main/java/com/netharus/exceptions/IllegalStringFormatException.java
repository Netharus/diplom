package com.netharus.exceptions;

public class IllegalStringFormatException extends RuntimeException {
    public IllegalStringFormatException(String s) {
        super(s);
    }
}
