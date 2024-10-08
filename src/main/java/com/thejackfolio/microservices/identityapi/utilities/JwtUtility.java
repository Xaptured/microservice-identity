package com.thejackfolio.microservices.identityapi.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service
public class JwtUtility {

    public static final String SECRET = PropertiesReader.getProperty(StringConstants.SECRET);

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        if (roles.contains(new SimpleGrantedAuthority("PARTICIPANT"))) {
            claims.put("isParticipant", true);
        }
        if (roles.contains(new SimpleGrantedAuthority("ORGANIZER"))) {
            claims.put("isOrganizer", true);
        }
        if (roles.contains(new SimpleGrantedAuthority("ADMIN"))) {
            claims.put("isAdmin", true);
        }
        if (roles.contains(new SimpleGrantedAuthority("AUDIENCE"))) {
            claims.put("isAudience", true);
        }
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public List<String> getRolesFromToken(String authToken) {
        List<String> roles = null;
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(authToken).getBody();
        Boolean isParticipant = claims.get("isParticipant", Boolean.class);
        Boolean isOrganizer = claims.get("isOrganizer", Boolean.class);
        Boolean isAdmin = claims.get("isAdmin", Boolean.class);
        Boolean isAudience = claims.get("isAudience", Boolean.class);
        if (isParticipant != null && isParticipant == true) {
            roles = Arrays.asList("PARTICIPANT");
        }
        if (isOrganizer != null && isOrganizer == true) {
            roles = Arrays.asList("ORGANIZER");
        }
        if (isAdmin != null && isAdmin == true) {
            roles = Arrays.asList("ADMIN");
        }
        if (isAudience != null && isAudience == true) {
            roles = Arrays.asList("AUDIENCE");
        }
        return roles;
    }
}
