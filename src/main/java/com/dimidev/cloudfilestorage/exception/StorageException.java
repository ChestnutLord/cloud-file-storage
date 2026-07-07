package com.dimidev.cloudfilestorage.exception;

import org.springframework.http.HttpStatus;

public class StorageException extends ApiException {

    public StorageException(String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }

    public StorageException(String reason, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause);
    }
}