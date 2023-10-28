package com.thejackfolio.microservices.identityapi.exceptions;

public class TokenException extends  Exception{

    public TokenException(String message){
        super(message);
    }
}
