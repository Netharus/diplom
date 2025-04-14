package com.netharus.domain.dto.response;

import lombok.Builder;

@Builder
public record GesturesDto(
        int id,
        String name,
        String title
) {
}
