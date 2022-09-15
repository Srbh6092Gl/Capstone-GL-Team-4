package com.Team4.project.jwt;

import com.Team4.project.service.serviceImpl.jwt.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final long serialVersionUID = 1L;

    @Value("${mbps.jwt.secretKey}")
    private String secretKey;

    @Value("${mbps.jwt.expirationTime}")
    private long expirationTime;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
//        Date date = new Date();
        Map<String,Object> claims = new HashMap<>();

        //Getting current time of the system in milliseconds
        final long currentTime = System.currentTimeMillis();

        String encodedString = Base64.getEncoder().encodeToString(secretKey.getBytes());

        System.err.println(secretKey);
        System.err.println(expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime+expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            System.err.println("JwtUtils/validateToken: valid");
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
