package com.dimidev.cloudfilestorage.repository.api;

import com.dimidev.cloudfilestorage.model.ListedResource;
import com.dimidev.cloudfilestorage.model.StoredFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface StorageRepository {

    void upload(StoredFile file);

    boolean exists(String objectName);

    Optional<ListedResource> findObject(String objectName);

    InputStream getObjectStream(String objectName);

    void createDirectory(String objectName);

    List<ListedResource> listObjects(String prefix);

    List<ListedResource> listObjectsRecursive(String prefix);

    void deleteObject(String objectName);

    void deleteObjects(List<String> objectNames);

    void copyObject(String sourceObjectName, String targetObjectName);
}
