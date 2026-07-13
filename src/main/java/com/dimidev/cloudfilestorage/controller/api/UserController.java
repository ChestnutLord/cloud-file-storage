package com.dimidev.cloudfilestorage.controller.api;

import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "User", description = "Информация о текущем пользователе")
public interface UserController {

    @Operation(summary = "Получение текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь получен"),
            @ApiResponse(responseCode = "401", description = "Неавторизован")
    })
    UserReadDto me(@AuthenticationPrincipal CustomUserDetails userDetails);
}
