package com.netharus.domain.dto.request;

import com.netharus.domain.enums.Role;
import lombok.Builder;

@Builder
public record AdminUserCreateDto(
        String username,
        String password,
        Role role,
        Boolean active
) {
}
