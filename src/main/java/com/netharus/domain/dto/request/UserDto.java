package com.netharus.domain.dto.request;

import lombok.Builder;

@Builder
public record UserDto(
        String username,
        String password
) {
}
