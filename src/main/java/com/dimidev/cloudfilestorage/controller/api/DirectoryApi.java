package com.dimidev.cloudfilestorage.controller.api;

import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Directory", description = "Управление папками")
public interface DirectoryApi {

    @Operation(summary = "Получение содержимого папки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Содержимое папки получено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "404", description = "Папка не существует")
    })
    List<ResourceResponse> list(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam(defaultValue = "") String path);

    @Operation(summary = "Создание пустой папки")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Папка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "404", description = "Родительская папка не существует"),
            @ApiResponse(responseCode = "409", description = "Папка уже существует")
    })
    ResponseEntity<ResourceResponse> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestParam String path);
}
