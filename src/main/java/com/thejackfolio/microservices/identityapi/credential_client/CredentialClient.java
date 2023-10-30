package com.thejackfolio.microservices.identityapi.credential_client;

import com.thejackfolio.microservices.identityapi.models.ClientCredential;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CLIENT-SERVICE")
public interface CredentialClient {

    @PostMapping("/credential/save-credential")
    public ResponseEntity<ClientCredential> saveCredential(@RequestBody ClientCredential credential);

    @GetMapping("/credential/get-credential/{email}")
    public ResponseEntity<ClientCredential> getCredential(@PathVariable String email);
}
