package com.bfs.authenticationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("$security.jwt.token.key")
    private String key;


    // create jwt from a UserDetail
    public String createToken(AuthUserDetail userDetails){
        //Claims is essentially a key-value pair, where the key is a string and the value is an object
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername()); // user identifier
        claims.put("permissions", userDetails.getAuthorities()); // user permission
        claims.put("id", userDetails.getId()); // user id"
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key) // algorithm and key to sign the token
                .compact();
    }
}
