package com.netharus.domain.enums;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    ADMIN("Администратор"),
    USER("Пользователь");

    private final String def;

    @Override
    public String getAuthority() {
        return name();
    }
}
