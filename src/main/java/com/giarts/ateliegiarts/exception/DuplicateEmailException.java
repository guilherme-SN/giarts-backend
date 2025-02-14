package com.giarts.ateliegiarts.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Email \"" + email + "\" already used");
    }
}
