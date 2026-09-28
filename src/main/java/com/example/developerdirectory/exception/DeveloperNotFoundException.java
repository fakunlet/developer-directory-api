package com.example.developerdirectory.exception;

public class DeveloperNotFoundException extends RuntimeException {

    public DeveloperNotFoundException(Integer id) {
        super("Developer with id " + id + " not found");
    }

    public DeveloperNotFoundException(String message) {
        super(message);
    }
}
