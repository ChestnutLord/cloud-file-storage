package com.dimidev.cloudfilestorage.minio.manager;

import com.dimidev.cloudfilestorage.config.MinioProperties;
import com.dimidev.cloudfilestorage.exception.StorageException;
import com.dimidev.cloudfilestorage.model.ListedResource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MinioQueryManager {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public Optional<ListedResource> findObject(String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .build()
            );
            return Optional.of(new ListedResource(
                    objectName,
                    stat.size(),
                    objectName.endsWith("/")
            ));
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return Optional.empty();
            }
            throw new StorageException("Не удалось получить объект из MinIO.", e);
        } catch (Exception e) {
            throw new StorageException("Не удалось получить объект из MinIO.", e);
        }
    }

    public boolean exists(String objectName) {
        return findObject(objectName).isPresent();
    }

    public InputStream getObjectStream(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new StorageException("Объект не найден в MinIO.", e);
            }
            throw new StorageException("Не удалось скачать объект из MinIO.", e);
        } catch (Exception e) {
            throw new StorageException("Не удалось скачать объект из MinIO.", e);
        }
    }

    public List<ListedResource> listObjects(String prefix) {
        return listObjects(prefix, false);
    }

    public List<ListedResource> listObjectsRecursive(String prefix) {
        return listObjects(prefix, true);
    }

    private List<ListedResource> listObjects(String prefix, boolean recursive) {
        List<ListedResource> resources = new ArrayList<>();

        try {
            ListObjectsArgs.Builder builder = ListObjectsArgs.builder()
                    .bucket(properties.getBucket())
                    .prefix(prefix)
                    .recursive(recursive);

            if (!recursive) {
                builder.delimiter("/");
            }

            for (Result<Item> result : minioClient.listObjects(builder.build())) {
                Item item = result.get();
                if (item.objectName().equals(prefix)) {
                    continue;
                }

                resources.add(new ListedResource(
                        item.objectName(),
                        item.size(),
                        item.isDir() || item.objectName().endsWith("/")
                ));
            }
        } catch (Exception e) {
            throw new StorageException("Не удалось получить список объектов из MinIO.", e);
        }

        return resources;
    }
}
