package com.netharus.controller.admin.rest;

import com.netharus.domain.dto.response.PageContainer;
import com.netharus.domain.dto.response.UserResponseDto;
import com.netharus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class RestAdminUserController {

    private final UserService userService;

    @PatchMapping("/changeStatus/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String changeStatus(@PathVariable Long userId) {
        userService.updateStatus(userId);
        return String.format("Статус пользователя с id: %d обновлен", userId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageContainer<UserResponseDto> users(@PageableDefault(sort = "active") Pageable pageable,
                                                @RequestParam(defaultValue = "") String keyword) {
        return userService.getPageContainer(pageable, keyword);
    }
}
