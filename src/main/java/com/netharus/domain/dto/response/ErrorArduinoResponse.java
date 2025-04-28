package com.netharus.domain.dto.response;

public record ErrorArduinoResponse(
        int statusCode,
        String message,
        Long userId
) {
}
