package com.dataFoot.ProjetData.exception.entitexception;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message, Throwable cause){

        super(message,cause);
    }
}
