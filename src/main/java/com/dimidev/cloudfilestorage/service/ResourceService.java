package com.dimidev.cloudfilestorage.service;

import com.dimidev.cloudfilestorage.dto.resource.ResourceResponse;
import com.dimidev.cloudfilestorage.exception.BadRequestException;
import com.dimidev.cloudfilestorage.exception.DuplicateResourceException;
import com.dimidev.cloudfilestorage.exception.NotFoundException;
import com.dimidev.cloudfilestorage.exception.StorageException;
import com.dimidev.cloudfilestorage.mapper.ResourceMapper;
import com.dimidev.cloudfilestorage.model.DownloadedResource;
import com.dimidev.cloudfilestorage.model.ListedResource;
import com.dimidev.cloudfilestorage.model.StoredFile;
import com.dimidev.cloudfilestorage.repository.api.StorageRepository;
import com.dimidev.cloudfilestorage.util.PathUtils;
import com.dimidev.cloudfilestorage.util.PathUtils.ResourcePathParts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final StorageRepository storageRepository;
    private final ResourceMapper resourceMapper;

    public List<ResourceResponse> upload(Long userId,
                                         String path,
                                         List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("Не переданы файлы для загрузки");
        }

        String targetFolder = PathUtils.normalizeDirectoryPath(path);
        ensureTargetFolderExists(userId, targetFolder);

        List<ResourceResponse> uploadedResources = new ArrayList<>();
        Set<String> ensuredDirectories = new HashSet<>();

        for (MultipartFile file : files) {
            uploadedResources.add(uploadFile(userId, targetFolder, file, ensuredDirectories));
        }

        return uploadedResources;
    }

    public ResourceResponse get(Long userId, String path) {
        String resourcePath = PathUtils.normalizeResourcePath(path);
        String storageKey = PathUtils.toStorageKey(userId, resourcePath);

        ListedResource resource = storageRepository.findObject(storageKey)
                .orElseThrow(() -> new NotFoundException("Ресурс не найден"));

        return resourceMapper.toResponse(resourcePath, resource);
    }

    public void delete(Long userId, String path) {
        String resourcePath = PathUtils.normalizeResourcePath(path);
        String storageKey = PathUtils.toStorageKey(userId, resourcePath);

        if (!storageRepository.exists(storageKey)) {
            throw new NotFoundException("Ресурс не найден");
        }

        if (PathUtils.isDirectoryPath(resourcePath)) {
            List<String> objectNames = new ArrayList<>();
            objectNames.add(storageKey);
            storageRepository.listObjectsRecursive(storageKey).stream()
                    .map(ListedResource::objectName)
                    .forEach(objectNames::add);
            storageRepository.deleteObjects(objectNames);
            return;
        }

        storageRepository.deleteObject(storageKey);
    }

    public DownloadedResource download(Long userId, String path) {
        String resourcePath = PathUtils.normalizeResourcePath(path);
        String storageKey = PathUtils.toStorageKey(userId, resourcePath);

        if (!storageRepository.exists(storageKey)) {
            throw new NotFoundException("Ресурс не найден");
        }

        if (PathUtils.isDirectoryPath(resourcePath)) {
            String directoryName = PathUtils.splitDirectoryPath(resourcePath).name();
            return new DownloadedResource(
                    directoryName + ".zip",
                    outputStream -> zipDirectory(storageKey, outputStream)
            );
        }

        String filename = PathUtils.splitFilePath(resourcePath).name();
        return new DownloadedResource(
                filename,
                outputStream -> {
                    try (InputStream objectStream = storageRepository.getObjectStream(storageKey)) {
                        objectStream.transferTo(outputStream);
                    }
                }
        );
    }

    public ResourceResponse move(Long userId, String from, String to) {
        String sourcePath = PathUtils.normalizeResourcePath(from);
        String targetPath = PathUtils.normalizeResourcePath(to);

        if (PathUtils.isDirectoryPath(sourcePath) != PathUtils.isDirectoryPath(targetPath)) {
            throw new BadRequestException("Невалидный путь");
        }

        String sourceKey = PathUtils.toStorageKey(userId, sourcePath);
        String targetKey = PathUtils.toStorageKey(userId, targetPath);

        if (!storageRepository.exists(sourceKey)) {
            throw new NotFoundException("Ресурс не найден");
        }

        if (storageRepository.exists(targetKey)) {
            throw new DuplicateResourceException("Ресурс уже существует");
        }

        String targetParent = PathUtils.isDirectoryPath(targetPath)
                ? PathUtils.splitDirectoryPath(targetPath).path()
                : PathUtils.splitFilePath(targetPath).path();
        ensureTargetFolderExists(userId, targetParent);

        if (PathUtils.isDirectoryPath(sourcePath)) {
            moveDirectory(sourceKey, targetKey);
        } else {
            storageRepository.copyObject(sourceKey, targetKey);
            storageRepository.deleteObject(sourceKey);
        }

        ListedResource moved = storageRepository.findObject(targetKey)
                .orElseThrow(() -> new StorageException("Не удалось переместить ресурс"));
        return resourceMapper.toResponse(targetPath, moved);
    }

    private void moveDirectory(String sourceKey, String targetKey) {
        storageRepository.createDirectory(targetKey);

        List<ListedResource> children = storageRepository.listObjectsRecursive(sourceKey);
        for (ListedResource child : children) {
            String childTargetKey = targetKey + child.objectName().substring(sourceKey.length());
            storageRepository.copyObject(child.objectName(), childTargetKey);
        }

        List<String> objectNames = new ArrayList<>();
        objectNames.add(sourceKey);
        children.stream()
                .map(ListedResource::objectName)
                .forEach(objectNames::add);
        storageRepository.deleteObjects(objectNames);
    }

    private void zipDirectory(String directoryKey, OutputStream responseStream) {
        List<ListedResource> children = storageRepository.listObjectsRecursive(directoryKey);

        try (ZipOutputStream zipStream = new ZipOutputStream(responseStream)) {
            for (ListedResource child : children) {
                String entryName = child.objectName().substring(directoryKey.length());
                if (entryName.isEmpty()) {
                    continue;
                }

                if (child.directory()) {
                    zipStream.putNextEntry(new ZipEntry(entryName));
                    zipStream.closeEntry();
                    continue;
                }

                zipStream.putNextEntry(new ZipEntry(entryName));
                try (InputStream objectStream = storageRepository.getObjectStream(child.objectName())) {
                    objectStream.transferTo(zipStream);
                }
                zipStream.closeEntry();
            }
        } catch (IOException e) {
            throw new StorageException("Не удалось создать zip-архив", e);
        }
    }

    private ResourceResponse uploadFile(Long userId,
                                        String targetFolder,
                                        MultipartFile file,
                                        Set<String> ensuredDirectories) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("Имя файла не может быть пустым");
        }

        String relativeFilePath = PathUtils.normalizeFilePath(targetFolder + originalFilename);
        String storageKey = PathUtils.toStorageKey(userId, relativeFilePath);

        if (storageRepository.exists(storageKey)) {
            throw new DuplicateResourceException("Файл уже существует");
        }

        String parentDirectory = PathUtils.splitFilePath(relativeFilePath).path();
        ensureDirectoryHierarchyExists(userId, parentDirectory, ensuredDirectories);

        try {
            StoredFile storedFile = new StoredFile(
                    storageKey,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );
            storageRepository.upload(storedFile);
        } catch (IOException e) {
            throw new StorageException("Ошибка чтения файла", e);
        }

        ResourcePathParts parts = PathUtils.splitFilePath(relativeFilePath);
        return resourceMapper.toFileResponse(parts.path(), parts.name(), file.getSize());
    }

    private void ensureTargetFolderExists(Long userId, String targetFolder) {
        if (targetFolder.isEmpty()) {
            return;
        }

        String storageKey = PathUtils.toStorageKey(userId, targetFolder);
        if (!storageRepository.exists(storageKey)) {
            throw new NotFoundException("Папка не существует");
        }
    }

    private void ensureDirectoryHierarchyExists(Long userId,
                                                String directoryPath,
                                                Set<String> ensuredDirectories) {
        if (directoryPath.isEmpty()) {
            return;
        }

        String[] segments = directoryPath.split("/");
        StringBuilder current = new StringBuilder();

        for (String segment : segments) {
            if (segment.isBlank()) {
                continue;
            }

            current.append(segment).append("/");
            String relativeDirectory = current.toString();

            if (ensuredDirectories.contains(relativeDirectory)) {
                continue;
            }

            String storageKey = PathUtils.toStorageKey(userId, relativeDirectory);
            if (!storageRepository.exists(storageKey)) {
                storageRepository.createDirectory(storageKey);
            }

            ensuredDirectories.add(relativeDirectory);
        }
    }
}
