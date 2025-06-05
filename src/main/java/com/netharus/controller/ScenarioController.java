package com.netharus.controller;

import com.netharus.controller.utilityForControllers.PageBuilder;
import com.netharus.domain.enums.Gestures;
import com.netharus.stringConstants.PageTitles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ScenarioController {

    private final PageBuilder pageBuilder;

    @GetMapping("/scenario")
    public ModelAndView scenario(@AuthenticationPrincipal UserDetails user) {
        return pageBuilder.builder()
                .username(user.getUsername())
                .pageTitle(PageTitles.SCENARIO_PAGE.getPageTitle())
                .fragment(PageTitles.SCENARIO_PAGE.getFragment())
                .gestures(Gestures.getAll())
                .build();
    }

}
