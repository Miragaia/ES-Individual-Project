package com.todolist.ToDoList.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.net.URL;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret_key}")
    private String jwtSigningKey;
    @Value("${jwt.expirationtime.access}")
    private int accessExpTime;
    @Value("${jwt.expirationtime.refresh}")
    private int refreshExpTime;

    @Value("${aws.cognito.jwks.url}")
    private String jwksUrl; // Cognito's JWKS URL


    // Method to get the public key from Cognito's JWKS
    private PublicKey getPublicKey(String kid) throws Exception {
        URL url = new URL(jwksUrl);
        JWKSet jwkSet = JWKSet.load(url.openStream());
        JWK jwk = jwkSet.getKeyByKeyId(kid);
        if (jwk == null) {
            throw new Exception("No matching key found for kid: " + kid);
        }
        return jwk.toRSAKey().toPublicKey();
    }

    // Method to validate JWT
    public boolean validateJwt(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String kid = signedJWT.getHeader().getKeyID();  // Get the 'kid' from the JWT header

            // Retrieve the public key from Cognito's JWKS
            PublicKey publicKey = getPublicKey(kid);

            // Verify the JWT using RSASSA with the public key
            signedJWT.verify(new RSASSAVerifier((RSAPublicKey) publicKey));

            // Optionally, validate claims
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return !claims.getExpirationTime().before(new Date()); // Check if token is expired

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String extractSubject(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user, accessExpTime);
    }

    public boolean isTokenValid(String token, UserDetails user) {
        final String username = user.getUsername();
        return extractSubject(token).equals(username) && !isTokenExpired(token);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails user, int expirationTime) {
        return Jwts.builder()
                .claims().empty().add(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime)).and()
                .signWith(getSigningKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSigningKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(UserDetails user) {
        Map<String, Object> extraClaims = new HashMap<>();
        // extra claim to distinguish it from the access token with this claim
        extraClaims.put("type", "refresh");
        return this.generateToken(extraClaims, user, refreshExpTime);
    }
    
    public boolean isRefreshTokenValid(UserDetails user, String refreshToken) {
        final Claims refClaims = extractAllClaims(refreshToken);
        boolean validRefreshToken = refClaims.containsKey("type") && refClaims.get("type").equals("refresh")
                && !isTokenExpired(refreshToken);
        return validRefreshToken;
    }
}