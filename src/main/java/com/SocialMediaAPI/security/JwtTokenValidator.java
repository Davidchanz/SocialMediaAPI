package com.SocialMediaAPI.security;

import com.SocialMediaAPI.exception.InvalidTokenRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;


@Component
public class JwtTokenValidator {

    @Autowired
    private JwtDecoder decoder;

    /**
     * Validates if a token satisfies the following properties
     * - Signature is not malformed
     * - Token hasn't expired
     * - Token is supported
     */
    public boolean validateToken(String authToken) {
        try {
            this.decoder.decode(authToken);
        }catch (JwtException ex){
            throw new InvalidTokenRequestException(ex.getMessage());
        }
        return true;
    }

}