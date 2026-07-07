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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserReadDto> registration(@Valid @RequestBody UserUpsertDto dto,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        UserReadDto user = authService.registration(dto, request, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Override
    @PostMapping("/sign-in")
    public UserReadDto login(@Valid @RequestBody UserAuthDto dto,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        return authService.login(dto, request, response);
    }
}
