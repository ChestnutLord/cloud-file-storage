package com.dimidev.cloudfilestorage.minio.manager;

import com.dimidev.cloudfilestorage.config.MinioProperties;
import com.dimidev.cloudfilestorage.exception.StorageException;
import com.dimidev.cloudfilestorage.model.StoredFile;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUploadManager {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public void upload(StoredFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(file.objectName())
                            .stream(file.inputStream(), file.size(), -1)
                            .contentType(file.contentType())
                            .build()
            );
        } catch (Exception e) {
            log.error("Ошибка загрузки объекта в MinIO: objectName={}", file.objectName(), e);
            throw new StorageException("Не удалось загрузить объект в MinIO.", e);
        }
    }

    public void createDirectory(String objectName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
        } catch (Exception e) {
            log.error("Ошибка создания директории в MinIO: objectName={}", objectName, e);
            throw new StorageException("Не удалось создать директорию в MinIO.", e);
        }
    }
}
