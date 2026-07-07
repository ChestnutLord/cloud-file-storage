package com.dimidev.cloudfilestorage.controller.api;

import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Resource", description = "Управление файлами")
public interface ResourceApi {

    @Operation(
            summary = "Загрузка файлов и папок",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Файлы успешно загружены"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "409", description = "Файл уже существует")
    })
    ResponseEntity<List<ResourceResponse>> upload(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestParam(defaultValue = "") String path,
                                                  @Parameter(description = "Файлы для загрузки")
                                                  @RequestPart("files")
                                                  List<MultipartFile> files
    );
}
