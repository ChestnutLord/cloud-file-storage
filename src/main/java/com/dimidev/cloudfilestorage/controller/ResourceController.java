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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController implements ResourceApi {

    private final ResourceService resourceService;

    @Override
    @GetMapping
    public ResourceResponse get(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam("path") String path) {
        return resourceService.get(userDetails.getId(), path);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @RequestParam("path") String path) {
        resourceService.delete(userDetails.getId(), path);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/move")
    public ResourceResponse move(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("from") String from,
                                 @RequestParam("to") String to) {
        return resourceService.move(userDetails.getId(), from, to);
    }

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
