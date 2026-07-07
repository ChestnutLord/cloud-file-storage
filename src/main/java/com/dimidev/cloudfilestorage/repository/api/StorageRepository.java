package com.dimidev.cloudfilestorage.repository.api;

import com.dimidev.cloudfilestorage.model.StoredFile;

public interface StorageRepository {

    void upload(StoredFile file);

    boolean exists(String objectName);

    void createDirectory(String objectName);
}
