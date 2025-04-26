package com.netharus.exceptions.handler;

import com.netharus.exceptions.AlreadyExistsException;
import com.netharus.exceptions.IllegalStringFormatException;
import com.netharus.stringConstants.ErrorMessages;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler({NumberFormatException.class})
    public String ioExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("exceptionMessage", ErrorMessages.NUMBER_FORMAT_EXCEPTION_MESSAGE);
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({IllegalStringFormatException.class})
    public String illegalStringFormatExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("exceptionMessage", ex.getMessage());
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({AlreadyExistsException.class})
    public String alreadyExistsExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("exceptionMessage", ex.getMessage());
        return "redirect:" + request.getHeader("referer");
    }

}

