package org.raf.sk.userservice.exception;

import org.springframework.http.HttpStatus;

public class AccessForbidden extends CustomException {

    public AccessForbidden(String message) {
        super(message, ErrorCode.ACCESS_FORBIDDEN, HttpStatus.FORBIDDEN);
    }
}
