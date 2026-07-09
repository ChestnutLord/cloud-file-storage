package com.dimidev.cloudfilestorage.model;

public record ListedResource(
        String objectName,
        long size,
        boolean directory) {
}
