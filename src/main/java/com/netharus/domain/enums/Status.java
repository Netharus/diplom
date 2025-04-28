package com.netharus.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    SUCCESS("Успешно"),
    ERROR("Ошибка");

    private final String title;
}
