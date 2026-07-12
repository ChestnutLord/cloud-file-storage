package com.dimidev.cloudfilestorage.repository;

import com.dimidev.cloudfilestorage.minio.manager.MinioCopyManager;
import com.dimidev.cloudfilestorage.minio.manager.MinioDeleteManager;
import com.dimidev.cloudfilestorage.minio.manager.MinioQueryManager;
import com.dimidev.cloudfilestorage.minio.manager.MinioUploadManager;
import com.dimidev.cloudfilestorage.model.ListedResource;
import com.dimidev.cloudfilestorage.model.StoredFile;
import com.dimidev.cloudfilestorage.repository.api.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MinioRepository implements StorageRepository {

    private final MinioUploadManager uploadManager;
    private final MinioQueryManager queryManager;
    private final MinioDeleteManager deleteManager;
    private final MinioCopyManager copyManager;

    @Override
    public void upload(StoredFile file) {
        uploadManager.upload(file);
    }

    @Override
    public boolean exists(String objectName) {
        return queryManager.exists(objectName);
    }

    @Override
    public Optional<ListedResource> findObject(String objectName) {
        return queryManager.findObject(objectName);
    }

    @Override
    public InputStream getObjectStream(String objectName) {
        return queryManager.getObjectStream(objectName);
    }

    @Override
    public void createDirectory(String objectName) {
        uploadManager.createDirectory(objectName);
    }

    @Override
    public List<ListedResource> listObjects(String prefix) {
        return queryManager.listObjects(prefix);
    }

    @Override
    public List<ListedResource> listObjectsRecursive(String prefix) {
        return queryManager.listObjectsRecursive(prefix);
    }

    @Override
    public void deleteObject(String objectName) {
        deleteManager.deleteObject(objectName);
    }

    @Override
    public void deleteObjects(List<String> objectNames) {
        deleteManager.deleteObjects(objectNames);
    }

    @Override
    public void copyObject(String sourceObjectName, String targetObjectName) {
        copyManager.copyObject(sourceObjectName, targetObjectName);
    }
}
