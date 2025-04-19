package com.netharus.controller;

import com.netharus.domain.enums.Gestures;
import com.netharus.service.EventService;
import com.netharus.service.ScenarioService;
import com.netharus.service.UserService;
import com.netharus.stringConstants.PageTitles;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final EventService eventService;
    private final UserService userService;

    @GetMapping("/scenario")
    public ModelAndView scenario(@AuthenticationPrincipal UserDetails user) {
        ModelAndView mav = new ModelAndView("homePage");
        mav.addAllObjects(Map.of("username", user.getUsername(),
                        "pageTitle", PageTitles.SCENARIO_PAGE.getPageTitle(),
                        "fragment", PageTitles.SCENARIO_PAGE.getFragment()
                )
        );
        mav.addObject("gestures", Gestures.getAll());
        return mav;
    }

    @PostMapping("/scenario")
    public String sendScenario(@RequestParam String scenario, HttpServletRequest request, @AuthenticationPrincipal UserDetails user) {
        String scenarioString = scenarioService.sendScenario(scenario);
        eventService.createEvent(scenarioString, userService.findByUsername(user.getUsername()));
        return "redirect:" + request.getHeader("referer");
    }
}
