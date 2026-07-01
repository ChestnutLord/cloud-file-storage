package com.dimidev.cloudfilestorage.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioConfiguration {

    private final MinioProperties minioProperties;

    @Bean("minioClient")
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getInternalUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
}