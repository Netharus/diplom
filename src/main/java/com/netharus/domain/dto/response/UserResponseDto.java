package com.netharus.domain.dto.response;

public record UserResponseDto(
        Long id,
        String username,
        Boolean active,
        String role
) {
}
