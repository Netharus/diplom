package com.netharus.controller;

import com.netharus.domain.enums.Gestures;
import com.netharus.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final EventService eventService;

    @GetMapping("/")
    public String redirectToHomePage() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("homePage");
        mav.addAllObjects(Map.of("username", "Andrew Smith",
                        "pageTitle", "HOME PAGE",
                        "fragment", "homePage"
                )
        );
        mav.addObject("gestures", Gestures.getAll());
        mav.addObject("events", eventService.getLastFiveEvents(1L));
        return mav;
    }

    @PostMapping("/gestures")
    public String sendGesture(@RequestParam Long gestureId, HttpServletRequest request) {
        log.info(gestureId.toString());
        return "redirect:" + request.getHeader("referer");
    }


}
