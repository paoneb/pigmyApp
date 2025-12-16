package com.pigmy.app.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtUtil {

   // private static final String SECRET_KEY = "mySecretKey123"; // use env variable in prod

   // Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final String SECRET_KEY = "13f760869577f85a5dceb39e69d83e1998898cbe662f27bc9689a5b61e8a061b2a8329af";


   // private static final long EXPIRATION = 1000 * 60 * 60; // 1 hour
   private static final long EXPIRATION= 1000 * 60 * 5;

    public String generateToken(String username, String bankCode) {
        return Jwts.builder()
                .setSubject(username)
                .claim("bankCode", bankCode)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

}
