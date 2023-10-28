package com.thejackfolio.microservices.identityapi.controllers;

import com.thejackfolio.microservices.identityapi.exceptions.DataBaseOperationException;
import com.thejackfolio.microservices.identityapi.exceptions.MapperException;
import com.thejackfolio.microservices.identityapi.exceptions.ValidationException;
import com.thejackfolio.microservices.identityapi.models.ClientCredential;
import com.thejackfolio.microservices.identityapi.services.IdentityService;
import com.thejackfolio.microservices.identityapi.utilities.StringConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identity")
public class IdentityController {

    @Autowired
    private IdentityService service;
    @Autowired
    private AuthenticationManager authenticationManager;

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
    public ResponseEntity<String> generateToken(@RequestBody ClientCredential credential) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential.getEmail(), credential.getPassword()));
        if(authenticate.isAuthenticated()){
            String token = service.generateToken(credential.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(StringConstants.INVALID_ACCESS);
        }

    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
        try{
            service.validateToken(token);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

}
