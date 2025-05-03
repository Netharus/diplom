package com.netharus.domain.dto.request;

import com.netharus.domain.enums.Role;
import lombok.Builder;

@Builder
public record AdminUserUpdateDto(
        Long id,
        String username,
        String password,
        Role role,
        Boolean active
) {
}
