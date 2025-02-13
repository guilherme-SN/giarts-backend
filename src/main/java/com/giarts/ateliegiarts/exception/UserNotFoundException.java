package com.giarts.ateliegiarts.exception;


import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId.toString());
    }
}
