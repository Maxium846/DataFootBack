package com.dataFoot.exception.entitexception;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message, Throwable cause){

        super(message,cause);
    }

    public ExternalApiException(String message) {
        super(message);
    }
}
