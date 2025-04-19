package com.netharus.controller;

import com.netharus.service.EventService;
import com.netharus.service.UserService;
import com.netharus.stringConstants.PageTitles;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/history")
public class HistoryController {

    private final EventService eventService;
    private final UserService userService;

    @GetMapping
    public ModelAndView history(@PageableDefault(sort = "dateTime", direction = Sort.Direction.DESC) Pageable pageable,
                                @AuthenticationPrincipal UserDetails user) {
        ModelAndView mav = new ModelAndView("homePage");
        mav.addAllObjects(Map.of("username", user.getUsername(),
                "pageTitle", PageTitles.HISTORY_PAGE.getPageTitle(),
                "fragment", PageTitles.HISTORY_PAGE.getFragment(),
                "pageContainer", eventService
                        .getPageContainer(pageable, userService.findByUsername(user
                                        .getUsername())
                                .getId()))
        );

        return mav;
    }
}
