package com.thejackfolio.microservices.identityapi.controllers;

import com.thejackfolio.microservices.identityapi.enums.Role;
import com.thejackfolio.microservices.identityapi.exceptions.DataBaseOperationException;
import com.thejackfolio.microservices.identityapi.exceptions.EmailException;
import com.thejackfolio.microservices.identityapi.exceptions.MapperException;
import com.thejackfolio.microservices.identityapi.exceptions.ValidationException;
import com.thejackfolio.microservices.identityapi.models.ClientCredential;
import com.thejackfolio.microservices.identityapi.services.CustomUserDetailsService;
import com.thejackfolio.microservices.identityapi.services.IdentityService;
import com.thejackfolio.microservices.identityapi.utilities.StringConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identity")
@Tag(name = "Identity", description = "Identity management APIs")
public class IdentityController {

    @Autowired
    private IdentityService service;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Operation(
            summary = "Save credentials",
            description = "Save credentials and gives the same credential response with a message which defines whether the request is successful or not."
    )
    @PostMapping("/register")
    public ResponseEntity<ClientCredential> saveClientCredential(@RequestBody ClientCredential credential) {
        ClientCredential response = null;
        try {
            response = service.saveClientCredential(credential);
        } catch (ValidationException | DataBaseOperationException | MapperException | EmailException exception) {
            if(credential == null){
                credential = new ClientCredential();
            }
            credential.setMessage(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(credential);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get JWT Token",
            description = "Get a JWT token with role name."
    )
    @PostMapping("/token")
    public ResponseEntity<ClientCredential> generateToken(@RequestBody ClientCredential credential) {
        try {
            final UserDetails userDetails = userDetailsService.loadUserByUsername(credential.getEmail());
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential.getEmail(), credential.getPassword()));
            if (authenticate.isAuthenticated()) {
                String token = service.generateToken(userDetails);
                credential.setMessage(token);
                String role = service.getRolesFromToken(token).get(0);
                if (Role.fromString(role) != null) {
                    credential.setRole(Role.fromString(role));
                }
                if (userDetails.isEnabled()) {
                    credential.setVerified(userDetails.isEnabled());
                }
                return ResponseEntity.status(HttpStatus.OK).body(credential);
            } else {
                credential.setMessage(StringConstants.INVALID_ACCESS);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(credential);
            }
        }
        catch (UsernameNotFoundException | BadCredentialsException exception) {
            String message = exception.getMessage();
            if(message.equals(StringConstants.INTERNAL_SERVER_ERROR)) {
                credential.setMessage(StringConstants.FALLBACK_MESSAGE);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(credential);
            }
            credential.setMessage(message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(credential);
        }
        catch (DisabledException exception) {
            credential.setMessage(StringConstants.ACCOUNT_NOT_VERIFIED);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(credential);
        }
    }
}
