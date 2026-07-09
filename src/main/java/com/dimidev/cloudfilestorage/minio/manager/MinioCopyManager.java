package com.dimidev.cloudfilestorage.minio.manager;

import com.dimidev.cloudfilestorage.config.MinioProperties;
import com.dimidev.cloudfilestorage.exception.StorageException;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
            throw new StorageException("Не удалось скопировать объект в MinIO.", e);
        }
    }
}
