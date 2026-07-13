package com.dimidev.cloudfilestorage.controller;

import com.dimidev.cloudfilestorage.controller.api.AuthController;
import com.dimidev.cloudfilestorage.dto.user.UserAuthDto;
import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.dto.user.UserUpsertDto;
import com.dimidev.cloudfilestorage.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    @PostMapping("/sign-up")
    public ResponseEntity<UserReadDto> registration(@Valid @RequestBody UserUpsertDto dto,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        log.debug("Получен запрос на регистрацию");
        UserReadDto user = authService.registration(dto, request, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Override
    @PostMapping("/sign-in")
    public UserReadDto login(@Valid @RequestBody UserAuthDto dto,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        log.debug("Получен запрос на вход");
        return authService.login(dto, request, response);
    }

    @Override
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Получен запрос на выход");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        log.info("Пользователь вышел из системы");
        return ResponseEntity.noContent().build();
    }
}
