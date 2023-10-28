package com.thejackfolio.microservices.identityapi.exceptions;

public class ValidationException extends Exception{

    public ValidationException(String message){
        super(message);
    }
}
