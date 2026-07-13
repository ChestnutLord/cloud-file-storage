package com.dimidev.cloudfilestorage.controller;

import com.dimidev.cloudfilestorage.controller.api.DirectoryController;
import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import com.dimidev.cloudfilestorage.service.DirectoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directory")
public class DirectoryControllerImpl implements DirectoryController {

    private final DirectoryService directoryService;

    @Override
    @GetMapping
    public List<ResourceResponse> list(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @RequestParam(value = "path", defaultValue = "") String path) {
        log.debug("Получен запрос на получение содержимого папки: userId={}, path={}",
                userDetails.getId(), path);
        return directoryService.list(userDetails.getId(), path);
    }

    @Override
    @PostMapping
    public ResponseEntity<ResourceResponse> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @RequestParam("path") String path) {
        log.debug("Получен запрос на создание папки: userId={}, path={}", userDetails.getId(), path);
        ResourceResponse directory = directoryService.create(userDetails.getId(), path);
        return ResponseEntity.status(HttpStatus.CREATED).body(directory);
    }
}
