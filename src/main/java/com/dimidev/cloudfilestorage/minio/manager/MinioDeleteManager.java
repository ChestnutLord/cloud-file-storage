package com.dimidev.cloudfilestorage.minio.manager;

import com.dimidev.cloudfilestorage.config.MinioProperties;
import com.dimidev.cloudfilestorage.exception.StorageException;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MinioDeleteManager {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public void deleteObject(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Не удалось удалить объект из MinIO.", e);
        }
    }

    public void deleteObjects(List<String> objectNames) {
        if (objectNames.isEmpty()) {
            return;
        }

        try {
            List<DeleteObject> objects = objectNames.stream()
                    .map(DeleteObject::new)
                    .toList();

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(properties.getBucket())
                            .objects(objects)
                            .build()
            );

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                throw new StorageException(
                        "Не удалось удалить объект из MinIO: " + error.objectName()
                );
            }
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Не удалось удалить объекты из MinIO.", e);
        }
    }
}
