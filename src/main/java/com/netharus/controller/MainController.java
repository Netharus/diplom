package com.netharus.controller;

import com.netharus.domain.enums.Gestures;
import com.netharus.service.EventService;
import com.netharus.service.GestureService;
import com.netharus.service.UserService;
import com.netharus.stringConstants.PageTitles;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MainController {

    private final EventService eventService;
    private final UserService userService;
    private final GestureService gestureService;

    @GetMapping("/")
    public String redirectToHomePage() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView home(@AuthenticationPrincipal UserDetails user) {
        ModelAndView mav = new ModelAndView("homePage");
        mav.addAllObjects(Map.of("username", user.getUsername(),
                        "pageTitle", PageTitles.HOME_PAGE.getPageTitle(),
                        "fragment", PageTitles.HOME_PAGE.getFragment()
                )
        );
        mav.addObject("gestures", Gestures.getAll());
        mav.addObject("events", eventService.getLastFiveEvents(userService.findByUsername(user.getUsername()).getId()));
        return mav;
    }

    @PostMapping("/gestures")
    public String sendGesture(@RequestParam int gestureId, HttpServletRequest request, @AuthenticationPrincipal UserDetails user) {
        eventService.createEvent(gestureService.sendGesture(gestureId), userService.findByUsername(user.getUsername()));
        return "redirect:" + request.getHeader("referer");
    }

}
