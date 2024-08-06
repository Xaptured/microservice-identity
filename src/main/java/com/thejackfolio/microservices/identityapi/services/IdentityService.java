package com.thejackfolio.microservices.identityapi.services;

import com.thejackfolio.microservices.identityapi.exceptions.*;
import com.thejackfolio.microservices.identityapi.models.ClientCredential;
import com.thejackfolio.microservices.identityapi.servicehelpers.IdentityServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdentityService {

    @Autowired
    private IdentityServiceHelper helper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ClientCredential saveClientCredential(ClientCredential credential) throws ValidationException, DataBaseOperationException, MapperException, EmailException {
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        return helper.saveClientCredential(credential);
    }

    public String generateToken(UserDetails userDetails) {
        return helper.generateToken(userDetails);
    }

    public List<String> getRolesFromToken(String token) {
        return helper.getRolesFromToken(token);
    }
}
