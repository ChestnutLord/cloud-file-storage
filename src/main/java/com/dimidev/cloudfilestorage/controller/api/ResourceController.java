package com.dimidev.cloudfilestorage.controller.api;

import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Tag(name = "Resource", description = "Управление файлами")
public interface ResourceController {

    @Operation(summary = "Получение информации о ресурсе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о ресурсе получена"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден")
    })
    ResourceResponse get(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @RequestParam
                         @NotBlank(message = "Путь обязателен")
                         String path);

    @Operation(summary = "Удаление ресурса")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ресурс удалён"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден")
    })
    ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam
                                @NotBlank(message = "Путь обязателен")
                                String path);

    @Operation(summary = "Скачивание ресурса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ресурс скачан",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден")
    })
    ResponseEntity<StreamingResponseBody> download(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @RequestParam
                                                   @NotBlank(message = "Путь обязателен")
                                                   String path);

    @Operation(summary = "Поиск ресурсов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Поиск выполнен"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован")
    })
    List<ResourceResponse> search(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestParam
                                  @NotBlank(message = "Поисковый запрос обязателен")
                                  String query);

    @Operation(summary = "Переименование или перемещение ресурса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ресурс перемещён"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден"),
            @ApiResponse(responseCode = "409", description = "Ресурс уже существует")
    })
    ResourceResponse move(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @RequestParam
                          @NotBlank(message = "Параметр from обязателен")
                          String from,
                          @RequestParam
                          @NotBlank(message = "Параметр to обязателен")
                          String to);

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
                                                  @RequestPart("object")
                                                  List<MultipartFile> files
    );
}
