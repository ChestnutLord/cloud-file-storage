package com.dimidev.cloudfilestorage.model;

import java.io.InputStream;

public record StoredFile(
        String objectName,
        InputStream inputStream,
        long size,
        String contentType) {
}
