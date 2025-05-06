package com.netharus.controller.rest;

import com.netharus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class RestAccountController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @GetMapping("/isPasswordValid")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isPasswordValid(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String password) {
        return passwordEncoder.matches(password, userService.findByUsername(userDetails.getUsername()).getPassword());
    }

    @PutMapping("/updatePassword")
    @ResponseStatus(HttpStatus.OK)
    public String updatePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String password) {
        userService.updatePassword(userService.findByUsername(userDetails.getUsername()), password);
        return "Пароль обновлен";
    }

    @GetMapping("/isUserViewTutor")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isUserViewTutor(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername()).isGuideViewed();
    }

    @PatchMapping("/guideViewed")
    @ResponseStatus(HttpStatus.OK)
    public void guideViewed(@AuthenticationPrincipal UserDetails userDetails) {
        userService.guideViewed(userDetails);
    }
}
