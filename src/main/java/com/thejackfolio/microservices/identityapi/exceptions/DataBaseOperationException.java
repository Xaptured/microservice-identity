/*
 * Copyright (c) 2023.
 * Created this for the project called "TheJackFolio"
 * All right reserved by Jack
 */

package com.thejackfolio.microservices.identityapi.exceptions;

public class DataBaseOperationException extends Exception {

    public DataBaseOperationException(String message){
        super(message);
    }
    public DataBaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
