package com.thejackfolio.microservices.identityapi.services;

import com.thejackfolio.microservices.identityapi.credential_client.CredentialClient;
import com.thejackfolio.microservices.identityapi.exceptions.DataBaseOperationException;
import com.thejackfolio.microservices.identityapi.models.ClientCredential;
import com.thejackfolio.microservices.identityapi.models.CustomUserDetails;
import com.thejackfolio.microservices.identityapi.utilities.StringConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CredentialClient client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResponseEntity<ClientCredential> response = client.getCredential(username);
        ClientCredential responseBody = response.getBody();
        UserDetails details = null;
        if(responseBody.getMessage().equals(StringConstants.DATABASE_ERROR) || responseBody.getMessage().equals(StringConstants.MAPPING_ERROR) || responseBody.getMessage().equals(StringConstants.VALIDATION_ERROR) || responseBody.getMessage().equals(StringConstants.FALLBACK_MESSAGE)){
            throw new UsernameNotFoundException(StringConstants.INTERNAL_SERVER_ERROR);
        } else if(responseBody.getMessage().equals(StringConstants.USERNAME_NOT_FOUND)) {
            throw new UsernameNotFoundException(StringConstants.USER_NOT_FOUND);
        } else {
            details = new CustomUserDetails(responseBody);
        }
        return details;
    }
}
