package com.dimidev.cloudfilestorage.minio.manager;

import com.dimidev.cloudfilestorage.config.MinioProperties;
import com.dimidev.cloudfilestorage.exception.StorageException;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioCopyManager {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public void copyObject(String sourceObjectName, String targetObjectName) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(targetObjectName)
                            .source(
                                    CopySource.builder()
                                            .bucket(properties.getBucket())
                                            .object(sourceObjectName)
                                            .build()
                            )
                            .build()
            );
        } catch (Exception e) {
            log.error("Ошибка копирования объекта в MinIO: from={}, to={}",
                    sourceObjectName, targetObjectName, e);
            throw new StorageException("Не удалось скопировать объект в MinIO.", e);
        }
    }
}
