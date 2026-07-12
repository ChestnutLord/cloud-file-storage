package com.dimidev.cloudfilestorage.model;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record DownloadedResource(
        String filename,
        StreamingResponseBody body) {
}
