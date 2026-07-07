package com.dimidev.cloudfilestorage.controller;

import com.dimidev.cloudfilestorage.controller.api.ResourceApi;
import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import com.dimidev.cloudfilestorage.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController implements ResourceApi {

    private final ResourceService resourceService;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ResourceResponse>> upload(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestParam(value = "path", defaultValue = "") String path,
                                                         @RequestPart("files") List<MultipartFile> files) {
        List<ResourceResponse> resources =
                resourceService.upload(userDetails.getId(), path, files);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resources);
    }
}
