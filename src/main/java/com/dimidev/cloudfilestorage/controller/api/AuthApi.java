package com.dimidev.cloudfilestorage.controller.api;

import com.dimidev.cloudfilestorage.dto.user.UserAuthDto;
import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.dto.user.UserUpsertDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Авторизация, регистрация  и логаут пользователя")
public interface AuthApi {

    @Operation(summary ="Регистрация пользователя")
    UserReadDto registration(@Valid UserUpsertDto dto, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "Авторизация пользователя")
    UserReadDto login(@Valid UserAuthDto dto, HttpServletRequest request, HttpServletResponse response);
}
