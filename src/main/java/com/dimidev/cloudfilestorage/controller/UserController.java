package com.dimidev.cloudfilestorage.controller;

import com.dimidev.cloudfilestorage.controller.api.UserApi;
import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController implements UserApi {

    @Override
    @GetMapping("/me")
    public UserReadDto me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new UserReadDto(userDetails.getUsername());
    }
}
