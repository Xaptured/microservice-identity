/*
 * Copyright (c) 2023.
 * Created this for the project called "TheJackFolio"
 * All right reserved by Jack
 */

package com.thejackfolio.microservices.identityapi.models;



import com.thejackfolio.microservices.identityapi.enums.Role;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ClientCredential {

        private String email;
        private String password;
        private Role role;
        private boolean verified;
        private String message;

    public ClientCredential() {
    }

    public ClientCredential(String email, String password, Role role, boolean verified, String message) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.verified = verified;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientCredential that = (ClientCredential) o;
        return verified == that.verified && Objects.equals(email, that.email) && Objects.equals(password, that.password) && role == that.role && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, role, verified, message);
    }

    @Override
    public String toString() {
        return "ClientCredential{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", verified=" + verified +
                ", message='" + message + '\'' +
                '}';
    }
}
