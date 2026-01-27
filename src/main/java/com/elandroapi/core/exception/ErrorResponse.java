package com.elandroapi.core.exception;

public record ErrorResponse(
        int status,
        String message
) {
}
