package com.dimidev.cloudfilestorage.dto.resource;

import com.dimidev.cloudfilestorage.model.ResourceType;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceResponse(
        String path,
        String name,
        Long size,
        ResourceType type) {
}
