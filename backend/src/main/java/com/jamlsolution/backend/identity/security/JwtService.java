package com.jamlsolution.backend.identity.security;

import com.jamlsolution.backend.identity.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    public static final String TOKEN_TYPE = "token_type";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public JwtService() throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey("keys/private_key.pem");
        this.publicKey = KeyUtils.loadPublicKey("keys/public_key.pem");
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "ACCESS_TOKEN");
        claims.put("userId", user.getId());
        return buildToken(user, claims, this.accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "REFRESH_TOKEN");
        claims.put("userId", user.getId());
        return buildToken(user, claims, this.refreshTokenExpiration);
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        String username = extractUsername(token);
        return username.equals(expectedUsername.trim()) && !isTokenExpired(token);
    }
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }



    private Claims extractClaims(String token) {
        try {
            return Jwts.parser().verifyWith(this.publicKey).build().parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    private String buildToken(User user, Map<String, Object> claims, long accessTokenExpiration) {
        return Jwts.builder().claims(claims).subject(user.getEmail()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + accessTokenExpiration)).signWith(this.privateKey).compact();
    }
}
