package com.netharus.exceptions.handler;

import com.netharus.exceptions.BadRequestException;
import com.netharus.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("❗ Async exception in method: {}", method.getName());
        if (ex instanceof BadRequestException) {
            log.warn("Некорректный запрос: {}", ex.getMessage());
        } else if (ex instanceof NotFoundException) {
            log.warn("Ресурс не найден: {}", ex.getMessage());
        } else {
            log.error("Необработанная ошибка: {}", ex.getMessage());
        }
    }
}

