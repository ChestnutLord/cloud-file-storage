package com.dimidev.cloudfilestorage.controller;

import com.dimidev.cloudfilestorage.controller.api.AuthApi;
import com.dimidev.cloudfilestorage.dto.user.UserAuthDto;
import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.dto.user.UserUpsertDto;
import com.dimidev.cloudfilestorage.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    @PostMapping("/sign-up")
    public UserReadDto registration(@RequestBody UserUpsertDto dto,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        return authService.registration(dto, request, response);
    }

    @Override
    @PostMapping("/sign-in")
    public UserReadDto login(@RequestBody UserAuthDto dto,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        return authService.login(dto, request, response);
    }
}
