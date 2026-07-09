package com.dimidev.cloudfilestorage.service;

import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.exception.BadRequestException;
import com.dimidev.cloudfilestorage.exception.DuplicateResourceException;
import com.dimidev.cloudfilestorage.exception.NotFoundException;
import com.dimidev.cloudfilestorage.model.ListedResource;
import com.dimidev.cloudfilestorage.model.ResourceType;
import com.dimidev.cloudfilestorage.repository.api.StorageRepository;
import com.dimidev.cloudfilestorage.util.PathUtils;
import com.dimidev.cloudfilestorage.util.PathUtils.ResourcePathParts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final StorageRepository storageRepository;

    public List<ResourceResponse> list(Long userId, String path) {
        String directoryPath = PathUtils.normalizeDirectoryPath(path);
        ensureDirectoryExists(userId, directoryPath);

        String storagePrefix = PathUtils.toStorageKey(userId, directoryPath);
        String userPrefix = PathUtils.userStoragePrefix(userId);

        return storageRepository.listObjects(storagePrefix).stream()
                .map(listedResource -> toResourceResponse(listedResource, userPrefix, directoryPath))
                .toList();
    }

    public ResourceResponse create(Long userId, String path) {
        if (path == null || path.isBlank()) {
            throw new BadRequestException("Невалидный путь");
        }

        String directoryPath = PathUtils.normalizeDirectoryPath(path);
        if (directoryPath.isEmpty()) {
            throw new BadRequestException("Невалидный путь");
        }

        ResourcePathParts parts = PathUtils.splitDirectoryPath(directoryPath);
        ensureParentDirectoryExists(userId, parts.path());

        String storageKey = PathUtils.toStorageKey(userId, directoryPath);
        if (storageRepository.exists(storageKey)) {
            throw new DuplicateResourceException("Папка уже существует");
        }

        storageRepository.createDirectory(storageKey);
        return new ResourceResponse(parts.path(), parts.name(), null, ResourceType.DIRECTORY);
    }

    private ResourceResponse toResourceResponse(ListedResource listedResource,
                                                String userPrefix,
                                                String directoryPath) {
        String itemName = listedResource.objectName().substring(userPrefix.length());

        if (listedResource.directory()) {
            String childRelative = itemName.substring(directoryPath.length());
            String name = childRelative.substring(0, childRelative.length() - 1);
            return new ResourceResponse(directoryPath, name, null, ResourceType.DIRECTORY);
        }

        ResourcePathParts parts = PathUtils.splitFilePath(itemName);
        return new ResourceResponse(
                parts.path(),
                parts.name(),
                listedResource.size(),
                ResourceType.FILE
        );
    }

    private void ensureDirectoryExists(Long userId, String directoryPath) {
        if (directoryPath.isEmpty()) {
            return;
        }

        String storageKey = PathUtils.toStorageKey(userId, directoryPath);
        if (!storageRepository.exists(storageKey)) {
            throw new NotFoundException("Папка не существует");
        }
    }

    private void ensureParentDirectoryExists(Long userId, String parentPath) {
        if (parentPath.isEmpty()) {
            return;
        }

        String storageKey = PathUtils.toStorageKey(userId, parentPath);
        if (!storageRepository.exists(storageKey)) {
            throw new NotFoundException("Родительская папка не существует");
        }
    }
}
