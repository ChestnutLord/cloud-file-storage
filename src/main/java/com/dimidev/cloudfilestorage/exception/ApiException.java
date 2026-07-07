package com.dimidev.cloudfilestorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApiException extends ResponseStatusException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String reason) {
        super(status, reason);
        this.status = status;
    }

    public ApiException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
        this.status = status;
    }

    public String getStatusMessage() {
        return status.getReasonPhrase();
    }
}
