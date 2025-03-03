package com.giarts.ateliegiarts.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;

public record ApiError(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Sao_Paulo")
        Instant timestamp,
        int status,
        String error,
        String message,
        List<String> details,
        String path
) {
    public ApiError(int status, String error, String message, List<String> details, String path) {
        this(Instant.now(), status, error, message, details, path);
    }
}
