package com.dimidev.cloudfilestorage.dto.resource;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record DownloadedResource(
        String filename,
        StreamingResponseBody body) {
}
