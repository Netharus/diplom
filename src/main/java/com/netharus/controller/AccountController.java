package com.netharus.controller;

import com.netharus.domain.dto.request.UserDto;
import com.netharus.service.UserService;
import com.netharus.stringConstants.RedirectMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping("/registration")
    public ModelAndView registration() {
        ModelAndView mav = new ModelAndView("auth/registration");
        mav.addObject("userDto", UserDto.builder().build());
        return mav;
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userDto") UserDto userDto, RedirectAttributes redirectAttributes) {
        userService.createAccount(userDto);

        redirectAttributes.addFlashAttribute("message",
                RedirectMessage.ACCOUNT_REGISTERED);

        return "redirect:/login";
    }
}
