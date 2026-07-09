package com.dimidev.cloudfilestorage.repository.api;

import com.dimidev.cloudfilestorage.model.ListedResource;
import com.dimidev.cloudfilestorage.model.StoredFile;

import java.util.List;

public interface StorageRepository {

    void upload(StoredFile file);

    boolean exists(String objectName);

    void createDirectory(String objectName);

    List<ListedResource> listObjects(String prefix);
}
