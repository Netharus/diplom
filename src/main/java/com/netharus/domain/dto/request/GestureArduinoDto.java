package com.netharus.domain.dto.request;

import lombok.Builder;

@Builder
public record GestureArduinoDto(
        Long userId,
        Integer gestureId
) {
}
