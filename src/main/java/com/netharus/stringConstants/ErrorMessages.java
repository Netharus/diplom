package com.netharus.stringConstants;

public final class ErrorMessages {

    public static final String NUMBER_FORMAT_EXCEPTION_MESSAGE = "Неправильный формат ID жеста.";
    public static final String ILLEGAL_STRING_FORMAT_EXCEPTION = "Неверный формат строки сценария";
    public static final String ILLEGAL_SCENARIO_FILE_FORMAT_EXCEPTION = "Неверный формат сценария";
    public static final String IO_EXCEPTION_MESSAGE = "Не удалось обработать файл";
    public static final String USER_NOT_FOUND = "Пользователь с username %s не найден";
    public static final String USER_ALREADY_EXIST = "Пользователь уже существует";
    public static final String USER_DISABLED = "Учетная запись не активирована";
    public static final String USERNAME_NOT_FOUND = "Пользователь с таким логином не найден";
    public static final String BAD_CREDENTIALS = "Неверный логин или пароль";
    public static final String ILLEGAL_GESTURE_ID = "Неверный id жеста. Жест с id: %s не существует";

    private ErrorMessages() {
    }
}
