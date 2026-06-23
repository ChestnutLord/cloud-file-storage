package com.dimidev.cloudfilestorage.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException{

    public NotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
}
