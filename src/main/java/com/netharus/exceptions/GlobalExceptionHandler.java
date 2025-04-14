package com.netharus.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NumberFormatException.class})
    public String ioExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("exceptionMessage", ErrorMessages.NUMBER_FORMAT_EXCEPTION_MESSAGE);
        return "redirect:" + request.getHeader("referer");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalStringFormatException.class})
    public String illegalStringFormatExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("exceptionMessage", ex.getMessage());
        return "redirect:" + request.getHeader("referer");
    }

}
