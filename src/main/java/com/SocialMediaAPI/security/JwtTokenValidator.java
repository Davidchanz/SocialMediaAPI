package com.SocialMediaAPI.security;

import com.SocialMediaAPI.exception.InvalidTokenRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenValidator {

    @Autowired
    private JwtDecoder decoder;

    private final String jwtSecret;
    //private final LoggedOutJwtTokenCache loggedOutTokenCache;

    @Autowired
    public JwtTokenValidator(@Value("${app.jwt.secret}") String jwtSecret/*, LoggedOutJwtTokenCache loggedOutTokenCache*/) {
        this.jwtSecret = jwtSecret;
        //this.loggedOutTokenCache = loggedOutTokenCache;
    }

    /**
     * Validates if a token satisfies the following properties
     * - Signature is not malformed
     * - Token hasn't expired
     * - Token is supported
     * - Token has not recently been logged out.
     */
    public boolean validateToken(String authToken) {
        try {
            this.decoder.decode(authToken);
            //Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);

        }catch (JwtException ex){
            throw new InvalidTokenRequestException(ex.getMessage());
        }
        /*catch (SignatureException ex) {
            System.out.println("Invalid JWT signature");
            throw new InvalidTokenRequestException("JWT", authToken, "Incorrect signature");

        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token");
            throw new InvalidTokenRequestException("JWT", authToken, "Malformed jwt token");

        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
            throw new InvalidTokenRequestException("JWT", authToken, "Token expired. Refresh required");

        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
            throw new InvalidTokenRequestException("JWT", authToken, "Unsupported JWT token");

        } */
        //validateTokenIsNotForALoggedOutDevice(authToken);
        return true;
    }

    /*private void validateTokenIsNotForALoggedOutDevice(String authToken) {
        OnUserLogoutSuccessEvent previouslyLoggedOutEvent = loggedOutTokenCache.getLogoutEventForToken(authToken);
        if (previouslyLoggedOutEvent != null) {
            String userEmail = previouslyLoggedOutEvent.getUserEmail();
            Date logoutEventDate = previouslyLoggedOutEvent.getEventTime();
            String errorMessage = String.format("Token corresponds to an already logged out user [%s] at [%s]. Please login again", userEmail, logoutEventDate);
            throw new InvalidTokenRequestException("JWT", authToken, errorMessage);
        }
    }*/
}