package com.dimidev.cloudfilestorage.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ApiException{

    public DuplicateResourceException(String reason) {
        super(HttpStatus.CONFLICT, reason);
    }
}
