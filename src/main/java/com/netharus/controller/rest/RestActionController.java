package com.netharus.controller.rest;

import com.netharus.domain.enums.Gestures;
import com.netharus.exceptions.InterruptedGestureException;
import com.netharus.lock.GlobalLock;
import com.netharus.service.GestureService;
import com.netharus.service.ScenarioService;
import com.netharus.service.UserService;
import com.netharus.stringConstants.ErrorMessages;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestActionController {

    private final ScenarioService scenarioService;
    private final UserService userService;
    private final GlobalLock globalLock;
    private final GestureService gestureService;


    @PostMapping("/scenario")
    public String sendScenario(@RequestParam String scenario,
                               HttpServletRequest request,
                               @AuthenticationPrincipal UserDetails user) {
        if (globalLock.tryLock()) {
            log.info("Блокировка для сценария установлена");
            scenarioService.sendScenario(userService.findByUsername(user.getUsername()).getId(), scenario);
        } else {
            throw new InterruptedGestureException(ErrorMessages.INTERRUPTED_GESTURE_EXCEPTION);
        }
        return String.format("Сценарий [%s] был отправлен", Gestures.getGestures(scenario));
    }


    @PostMapping("/gestures")
    public String sendGesture(@RequestParam Integer gestureId,
                              HttpServletRequest request,
                              @AuthenticationPrincipal UserDetails user) {
        if (globalLock.tryLock()) {
            log.info("Блокировка для жеста установлена");
            gestureService.sendGesture(userService.findByUsername(user.getUsername()).getId(), gestureId);
        } else {
            throw new InterruptedGestureException(ErrorMessages.INTERRUPTED_GESTURE_EXCEPTION);
        }
        return String.format("Жесты %s был отправлен", Gestures.getGestureTitle(gestureId));
    }
}
