package com.netharus.exceptions;

import com.netharus.domain.dto.reponse.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
