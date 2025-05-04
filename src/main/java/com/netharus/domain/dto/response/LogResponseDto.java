package com.netharus.domain.dto.response;

import lombok.Builder;

@Builder
public record LogResponseDto(
        Long id,
        String dateTime,
        String status,
        Long userId,
        String username,
        String message
) {
}
