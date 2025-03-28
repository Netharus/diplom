package com.netharus.controller;

import com.netharus.service.LogService;
import com.netharus.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final LogService logService;
    private final UserService userService;
}
