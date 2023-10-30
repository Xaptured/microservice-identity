package com.thejackfolio.microservices.identityapi.servicehelpers;

import com.thejackfolio.microservices.identityapi.credential_client.CredentialClient;
import com.thejackfolio.microservices.identityapi.exceptions.DataBaseOperationException;
import com.thejackfolio.microservices.identityapi.exceptions.MapperException;
import com.thejackfolio.microservices.identityapi.exceptions.TokenException;
import com.thejackfolio.microservices.identityapi.exceptions.ValidationException;
import com.thejackfolio.microservices.identityapi.models.ClientCredential;
import com.thejackfolio.microservices.identityapi.utilities.JwtUtility;
import com.thejackfolio.microservices.identityapi.utilities.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdentityServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityServiceHelper.class);
    @Autowired
    private CredentialClient client;
    @Autowired
    private JwtUtility jwtUtility;

    public ClientCredential saveClientCredential(ClientCredential credential) throws DataBaseOperationException, MapperException, ValidationException {
        ResponseEntity<ClientCredential> response = client.saveCredential(credential);
        ClientCredential responseBody = response.getBody();
        if(responseBody.getMessage().equals(StringConstants.DATABASE_ERROR)){
            throw new DataBaseOperationException(responseBody.getMessage());
        } else if(responseBody.getMessage().equals(StringConstants.MAPPING_ERROR)){
            throw new MapperException(responseBody.getMessage());
        } else if(responseBody.getMessage().equals(StringConstants.VALIDATION_ERROR)){
            throw new ValidationException(responseBody.getMessage());
        }
        // TODO: Add email exception while implementing orchestrate-service flow
        return responseBody;
    }

    public String generateToken(UserDetails userDetails) {
        return jwtUtility.generateToken(userDetails);
    }

    public List<String> getRolesFromToken(String token){
        return jwtUtility.getRolesFromToken(token);
    }
}
