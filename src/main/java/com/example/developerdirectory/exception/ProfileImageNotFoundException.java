package com.example.developerdirectory.exception;

public class ProfileImageNotFoundException extends RuntimeException {

    public ProfileImageNotFoundException(Integer developerId) {
        super("Developer " + developerId + " has no profile image");
    }
}
