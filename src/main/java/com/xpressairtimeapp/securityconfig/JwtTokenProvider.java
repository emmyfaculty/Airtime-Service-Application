package com.xpressairtimeapp.securityconfig;

import com.xpressairtimeapp.exceptions.TokenValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;
    @Value("${app.jwt-expiration}")
    private Long jwtExpirationDate;

    /**
     * Generates a JWT token based on the user's authentication details.
     *
     * @param authentication the user's authentication object
     * @return a signed JWT token
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(Key())
                .compact();
    }

    /**
     * Retrieves the signing key for the JWT token.
     *
     * @return the key used to sign the JWT token
     */
    private Key Key() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validates the JWT token by checking its structure and signature.
     *
     * @param token the JWT token
     * @return true if valid, otherwise false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Key())
                    .build()
                    .parse(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenValidationException("JWT token has expired");
        } catch (SignatureException e) {
            throw new TokenValidationException("JWT signature does not match");
        } catch (MalformedJwtException e) {
            throw new TokenValidationException("Invalid JWT token");
        } catch (IllegalArgumentException e) {
            throw new TokenValidationException("JWT claims string is empty");
        }
    }

}
