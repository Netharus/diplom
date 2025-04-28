package com.netharus.exceptions.handler;

import com.netharus.domain.dto.response.ErrorResponseDto;
import com.netharus.exceptions.BadRequestException;
import com.netharus.exceptions.IllegalScenarioFileFormatException;
import com.netharus.exceptions.InterruptedGestureException;
import com.netharus.exceptions.NotFoundException;
import com.netharus.stringConstants.ErrorMessages;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IOException.class})
    public ErrorResponseDto iOExceptionHandler(IOException ex, HttpServletRequest request) {
        log.error(ex.getMessage());
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST, ErrorMessages.IO_EXCEPTION_MESSAGE, request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalScenarioFileFormatException.class})
    public ErrorResponseDto illegalScenarioFileFormatExceptionHandler(Exception ex, HttpServletRequest request) {
        log.error(ex.getMessage());
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.LOCKED)
    @ExceptionHandler({InterruptedGestureException.class})
    public ErrorResponseDto interruptedGestureExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponseDto(HttpStatus.LOCKED, ex.getMessage(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public ErrorResponseDto notFoundExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponseDto(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class})
    public ErrorResponseDto badRequestExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({RetryableException.class})
    public ErrorResponseDto retryableExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error("Не удалось подключиться, сервер недоступен", ex);
        return new ErrorResponseDto(HttpStatus.SERVICE_UNAVAILABLE, "Не удалось подключиться, сервер недоступен", request.getRequestURI());
    }
}
