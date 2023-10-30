package com.thejackfolio.microservices.identityapi.controllers;

import com.thejackfolio.microservices.identityapi.enums.Role;
import com.thejackfolio.microservices.identityapi.exceptions.DataBaseOperationException;
import com.thejackfolio.microservices.identityapi.exceptions.MapperException;
import com.thejackfolio.microservices.identityapi.exceptions.ValidationException;
import com.thejackfolio.microservices.identityapi.models.ClientCredential;
import com.thejackfolio.microservices.identityapi.models.CustomUserDetails;
import com.thejackfolio.microservices.identityapi.services.CustomUserDetailsService;
import com.thejackfolio.microservices.identityapi.services.IdentityService;
import com.thejackfolio.microservices.identityapi.utilities.StringConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/identity")
public class IdentityController {

    @Autowired
    private IdentityService service;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<ClientCredential> saveClientCredential(@RequestBody ClientCredential credential) {
        ClientCredential response = null;
        try {
            response = service.saveClientCredential(credential);
        } catch (ValidationException | DataBaseOperationException | MapperException exception) {
            if(credential == null){
                credential = new ClientCredential();
            }
            credential.setMessage(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(credential);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/token")
    public ResponseEntity<ClientCredential> generateToken(@RequestBody ClientCredential credential) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential.getEmail(), credential.getPassword()));
        if(authenticate.isAuthenticated()){
            final UserDetails userDetails = userDetailsService.loadUserByUsername(credential.getEmail());
            String token = service.generateToken(userDetails);
            credential.setMessage(token);
            String role = service.getRolesFromToken(token).get(0);
            if(Role.fromString(role) != null){
                credential.setRole(Role.fromString(role));
            }
            credential.setRole(Role.fromString(role));
            return ResponseEntity.status(HttpStatus.OK).body(credential);
        } else {
            credential.setMessage(StringConstants.INVALID_ACCESS);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(credential);
        }

    }
}