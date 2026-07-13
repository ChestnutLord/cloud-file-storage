package com.dimidev.cloudfilestorage.controller;

import com.dimidev.cloudfilestorage.controller.api.UserController;
import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserControllerImpl implements UserController {

    @Override
    @GetMapping("/me")
    public UserReadDto me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.debug("Получен запрос на получение текущего пользователя: userId={}", userDetails.getId());
        return new UserReadDto(userDetails.getUsername());
    }
}
