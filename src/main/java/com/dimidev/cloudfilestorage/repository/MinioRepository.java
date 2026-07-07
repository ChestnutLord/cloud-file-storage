package com.dimidev.cloudfilestorage.repository;

import com.dimidev.cloudfilestorage.config.MinioProperties;
import com.dimidev.cloudfilestorage.exception.StorageException;
import com.dimidev.cloudfilestorage.model.StoredFile;
import com.dimidev.cloudfilestorage.repository.api.StorageRepository;
import io.minio.errors.ErrorResponseException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;

@Repository
@RequiredArgsConstructor
public class MinioRepository implements StorageRepository {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
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
            throw new StorageException("Не удалось загрузить объект в MinIO.", e);
        }
    }

    @Override
    public boolean exists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            throw new StorageException("Не удалось проверить существование объекта в MinIO.", e);
        } catch (Exception e) {
            throw new StorageException("Не удалось проверить существование объекта в MinIO.", e);
        }
    }

    @Override
    public void createDirectory(String objectName) {
        if (!objectName.endsWith("/")) {
            throw new IllegalArgumentException("Имя директории должно заканчиваться на '/'");
        }

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Не удалось создать директорию в MinIO.", e);
        }
    }
}
