package com.dimidev.cloudfilestorage.qafordevs.it;

import io.minio.MinioClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public abstract class IntegrationTestBase {

    @MockitoBean
    private MinioClient minioClient;

    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("minio.bucket", () -> "test-bucket");
        registry.add("minio.internalUrl", () -> "http://localhost:9000");
    }
}