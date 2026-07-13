package com.dimidev.cloudfilestorage.dto.resource;

import com.dimidev.cloudfilestorage.model.ResourceType;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceResponse(
        String path,
        String name,
        Long size,
        ResourceType type) {

    public static ResourceResponse file(String path, String name, long size) {
        return new ResourceResponse(path, name, size, ResourceType.FILE);
    }

    public static ResourceResponse directory(String path, String name) {
        return new ResourceResponse(path, name, null, ResourceType.DIRECTORY);
    }
}
