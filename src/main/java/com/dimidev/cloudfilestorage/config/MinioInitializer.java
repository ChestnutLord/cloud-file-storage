package com.dimidev.cloudfilestorage.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioInitializer implements ApplicationRunner {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String bucketName = minioProperties.getBucket();
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Storage bootstrap: Bucket '{}' created successfully.", bucketName);
            } else {
                log.info("Storage bootstrap: Bucket '{}' already exists.", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket: {}", e.getMessage());
            throw new IllegalStateException("Не удалось инициализировать MinIO", e);
        }
    }
}