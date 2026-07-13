package com.dimidev.cloudfilestorage.controller.api;

import com.dimidev.cloudfilestorage.dto.common.ErrorResponse;
import com.dimidev.cloudfilestorage.dto.user.UserAuthDto;
import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.dto.user.UserUpsertDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication", description = "Авторизация, регистрация и логаут пользователя")
public interface AuthApi {

    @Operation(summary = "Регистрация пользователя")
    ResponseEntity<UserReadDto> registration(@Valid UserUpsertDto dto,
                                             HttpServletRequest request,
                                             HttpServletResponse response);

    @Operation(summary = "Авторизация пользователя")
    UserReadDto login(@Valid UserAuthDto dto,
                      HttpServletRequest request,
                      HttpServletResponse response);

    @Operation(summary = "Выход из аккаунта")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешный выход"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response);
}
