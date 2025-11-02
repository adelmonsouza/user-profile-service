package com.adelmonsouza.userprofileservice.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email já está em uso: " + email);
    }
}

