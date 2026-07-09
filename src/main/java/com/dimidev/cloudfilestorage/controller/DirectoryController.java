package com.dimidev.cloudfilestorage.controller;

import com.dimidev.cloudfilestorage.controller.api.DirectoryApi;
import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import com.dimidev.cloudfilestorage.service.DirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directory")
public class DirectoryController implements DirectoryApi {

    private final DirectoryService directoryService;

    @Override
    @GetMapping
    public List<ResourceResponse> list(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @RequestParam(value = "path", defaultValue = "") String path) {
        return directoryService.list(userDetails.getId(), path);
    }

    @Override
    @PostMapping
    public ResponseEntity<ResourceResponse> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @RequestParam("path") String path) {
        ResourceResponse directory = directoryService.create(userDetails.getId(), path);
        return ResponseEntity.status(HttpStatus.CREATED).body(directory);
    }
}
