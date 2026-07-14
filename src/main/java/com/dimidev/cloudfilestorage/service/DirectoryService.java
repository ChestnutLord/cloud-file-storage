package com.dimidev.cloudfilestorage.service;

import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.exception.BadRequestException;
import com.dimidev.cloudfilestorage.exception.DuplicateResourceException;
import com.dimidev.cloudfilestorage.exception.NotFoundException;
import com.dimidev.cloudfilestorage.mapper.ResourceMapper;
import com.dimidev.cloudfilestorage.repository.api.StorageRepository;
import com.dimidev.cloudfilestorage.util.PathUtils;
import com.dimidev.cloudfilestorage.util.PathUtils.ResourcePathParts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final StorageRepository storageRepository;
    private final ResourceMapper resourceMapper;

    public List<ResourceResponse> list(Long userId, String path) {
        String directoryPath = PathUtils.normalizeDirectoryPath(path);
        ensureDirectoryExists(userId, directoryPath);

        String storagePrefix = PathUtils.toStorageKey(userId, directoryPath);
        String userPrefix = PathUtils.userStoragePrefix(userId);

        return storageRepository.listObjects(storagePrefix).stream()
                .map(listedResource -> resourceMapper.toResponse(listedResource, userPrefix, directoryPath))
                .toList();
    }

    public ResourceResponse create(Long userId, String path) {
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
        log.info("Папка создана: userId={}, path={}", userId, directoryPath);
        return resourceMapper.toDirectoryResponse(parts.path(), parts.name());
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
