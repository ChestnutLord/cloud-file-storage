package com.dimidev.cloudfilestorage.mapper;

import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.model.ListedResource;
import com.dimidev.cloudfilestorage.model.ResourceType;
import com.dimidev.cloudfilestorage.util.PathUtils;
import com.dimidev.cloudfilestorage.util.PathUtils.ResourcePathParts;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public ResourceResponse toResponse(String resourcePath, ListedResource resource) {
        if (resource.directory()) {
            ResourcePathParts parts = PathUtils.splitDirectoryPath(resourcePath);
            return toDirectoryResponse(parts.path(), parts.name());
        }

        ResourcePathParts parts = PathUtils.splitFilePath(resourcePath);
        return toFileResponse(parts.path(), parts.name(), resource.size());
    }

    public ResourceResponse toResponse(ListedResource listedResource,
                                       String userPrefix,
                                       String directoryPath) {
        String itemName = listedResource.objectName().substring(userPrefix.length());

        if (listedResource.directory()) {
            String childRelative = itemName.substring(directoryPath.length());
            String name = childRelative.substring(0, childRelative.length() - 1);
            return toDirectoryResponse(directoryPath, name);
        }

        ResourcePathParts parts = PathUtils.splitFilePath(itemName);
        return toFileResponse(parts.path(), parts.name(), listedResource.size());
    }

    public ResourceResponse toFileResponse(String path, String name, Long size) {
        return new ResourceResponse(path, name, size, ResourceType.FILE);
    }

    public ResourceResponse toDirectoryResponse(String path, String name) {
        return new ResourceResponse(path, name, null, ResourceType.DIRECTORY);
    }
}
