package com.dimidev.cloudfilestorage.dto.resource;

import com.dimidev.cloudfilestorage.model.ResourceType;

public record ResourceResponse(
        String path,
        String name,
        Long size,
        ResourceType type){
}
