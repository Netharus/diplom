package com.netharus.controller;

import com.netharus.domain.enums.Gestures;
import com.netharus.service.ScenarioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/scenario")
    public ModelAndView scenario() {
        ModelAndView mav = new ModelAndView("homePage");
        mav.addAllObjects(Map.of("username", "Andrew Smith",
                        "pageTitle", "СЦЕНАРИЙ",
                        "fragment", "scenario"
                )
        );
        mav.addObject("gestures", Gestures.getAll());
        return mav;
    }

    @PostMapping("/scenario")
    public String sendScenario(@RequestParam String scenario, HttpServletRequest request) {
        scenarioService.sendScenario(scenario);
        return "redirect:" + request.getHeader("referer");
    }
}
