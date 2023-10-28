package com.thejackfolio.microservices.identityapi.services;

import com.thejackfolio.microservices.identityapi.exceptions.DataBaseOperationException;
import com.thejackfolio.microservices.identityapi.exceptions.MapperException;
import com.thejackfolio.microservices.identityapi.exceptions.TokenException;
import com.thejackfolio.microservices.identityapi.exceptions.ValidationException;
import com.thejackfolio.microservices.identityapi.models.ClientCredential;
import com.thejackfolio.microservices.identityapi.servicehelpers.IdentityServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class IdentityService {

    @Autowired
    private IdentityServiceHelper helper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ClientCredential saveClientCredential(ClientCredential credential) throws ValidationException, DataBaseOperationException, MapperException {
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        return helper.saveClientCredential(credential);
    }

    public String generateToken(String userName) {
        return helper.generateToken(userName);
    }

    public void validateToken(String token) throws TokenException {
        helper.validateToken(token);
    }
}
