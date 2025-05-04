package com.netharus.controller.admin.rest;

import com.netharus.domain.dto.response.LogResponseDto;
import com.netharus.domain.dto.response.PageContainer;
import com.netharus.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/admin/logs")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class RestAdminLogController {

    private final LogService logService;

    @GetMapping
    public PageContainer<LogResponseDto> getLogs(@PageableDefault(sort = "dateTime",
            direction = Sort.Direction.DESC) Pageable pageable,
                                                 @RequestParam(defaultValue = "") String keyword) {
        return logService.getPageContainer(pageable, keyword);
    }
}
