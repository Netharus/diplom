package com.netharus.stringConstants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PageTitles {
    HOME_PAGE("ГЛАВНАЯ СТРАНИЦА", "homePage"),
    SCENARIO_PAGE("СЦЕНАРИЙ", "scenario"),
    HISTORY_PAGE("ИСТОРИЯ", "history"),
    USERS_PAGE("ПОЛЬЗОВАТЕЛИ", "users"),
    LOGS_PAGE("ЛОГИ", "logs");

    private final String pageTitle;
    private final String fragment;
}
