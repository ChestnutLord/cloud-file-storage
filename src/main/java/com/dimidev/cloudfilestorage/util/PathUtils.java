package com.dimidev.cloudfilestorage.util;

import com.dimidev.cloudfilestorage.exception.BadRequestException;

public final class PathUtils {

    private PathUtils() {
    }

    public static String normalizeDirectoryPath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }

        String normalized = normalizeSlashes(path.trim());
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (!normalized.isEmpty() && !normalized.endsWith("/")) {
            normalized = normalized + "/";
        }

        validateRelativePath(normalized);
        return normalized;
    }

    public static String normalizeFilePath(String path) {
        if (path == null || path.isBlank()) {
            throw new BadRequestException("Невалидный путь");
        }

        String normalized = normalizeSlashes(path.trim());
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        validateRelativePath(normalized);
        return normalized;
    }

    public static void validateRelativePath(String path) {
        if (path.contains("..")) {
            throw new BadRequestException("Невалидный путь");
        }

        if (path.contains("//")) {
            throw new BadRequestException("Невалидный путь");
        }

        String pathWithoutTrailingSlash = path.endsWith("/")
                ? path.substring(0, path.length() - 1)
                : path;

        if (pathWithoutTrailingSlash.isEmpty()) {
            return;
        }

        for (String segment : pathWithoutTrailingSlash.split("/")) {
            if (segment.isBlank()) {
                throw new BadRequestException("Невалидный путь");
            }
        }
    }

    public static String toStorageKey(long userId, String relativePath) {
        return "user-" + userId + "-files/" + relativePath;
    }

    public static String extractParentDirectory(String relativeFilePath) {
        int lastSlash = relativeFilePath.lastIndexOf('/');
        if (lastSlash < 0) {
            return "";
        }
        return relativeFilePath.substring(0, lastSlash + 1);
    }

    public static ResourcePathParts splitFilePath(String relativeFilePath) {
        int lastSlash = relativeFilePath.lastIndexOf('/');
        if (lastSlash < 0) {
            return new ResourcePathParts("", relativeFilePath);
        }
        return new ResourcePathParts(
                relativeFilePath.substring(0, lastSlash + 1),
                relativeFilePath.substring(lastSlash + 1)
        );
    }

    private static String normalizeSlashes(String path) {
        return path.replace('\\', '/');
    }

    public record ResourcePathParts(String path, String name) {
    }
}
