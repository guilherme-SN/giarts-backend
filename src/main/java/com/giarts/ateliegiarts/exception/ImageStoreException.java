package com.giarts.ateliegiarts.exception;

public class ImageStoreException extends RuntimeException {
    public ImageStoreException(String message) {
        super(message);
    }

    public ImageStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
