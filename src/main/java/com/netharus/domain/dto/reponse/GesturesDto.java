package com.netharus.domain.dto.reponse;

import lombok.Builder;

@Builder
public record GesturesDto(
        int id,
        String name,
        String title
) {
}
