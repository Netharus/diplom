package com.netharus.controller.admin;

import com.netharus.controller.utilityForControllers.PageBuilder;
import com.netharus.service.UserService;
import com.netharus.stringConstants.PageTitles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final PageBuilder pageBuilder;

    @GetMapping()
    public ModelAndView users(@PageableDefault(sort = "active") Pageable pageable,
                              @RequestParam(defaultValue = "") String keyword,
                              @AuthenticationPrincipal UserDetails user) {
        return pageBuilder.builder()
                .username(user.getUsername())
                .pageTitle(PageTitles.USERS_PAGE.getPageTitle())
                .fragment(PageTitles.USERS_PAGE.getFragment())
                .pageContainer(userService.getPageContainer(pageable, keyword))
                .build();
    }
}
