package com.netharus.domain.dto.response;

public record ArduinoResponseDto(
        Long userId,
        String message
) {
    public ArduinoResponseDto(String message) {
        this(null, message);
    }
}
