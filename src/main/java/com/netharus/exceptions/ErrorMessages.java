package com.netharus.exceptions;

public final class ErrorMessages {
    private ErrorMessages() {
    }

    public static final String NUMBER_FORMAT_EXCEPTION_MESSAGE = "Неправильный ID жест. ID должен быть в промежутке [1,8]";
    public static final String ILLEGAL_STRING_FORMAT_EXCEPTION = "Неверный формат строки сценария";
    public static final String ILLEGAL_SCENARIO_FILE_FORMAT_EXCEPTION = "Неверный формат сценария, файл должен содержать";
    public static final String IO_EXCEPTION_MESSAGE = "Не удалось обработать файл";
}
