package com.netharus.controller.admin;

import com.netharus.controller.utilityForControllers.PageBuilder;
import com.netharus.service.LogService;
import com.netharus.stringConstants.PageTitles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin/logs")
public class AdminLogController {

    private final LogService logService;
    private final PageBuilder pageBuilder;

    @GetMapping()
    public ModelAndView logs(@PageableDefault(sort = "dateTime", direction = Sort.Direction.DESC) Pageable pageable,
                             @RequestParam(defaultValue = "") String keyword,
                             @AuthenticationPrincipal UserDetails user) {
        return pageBuilder.builder()
                .username(user.getUsername())
                .pageTitle(PageTitles.LOGS_PAGE.getPageTitle())
                .fragment(PageTitles.LOGS_PAGE.getFragment())
                .pageContainer(logService.getPageContainer(pageable, keyword))
                .build();
    }
}
