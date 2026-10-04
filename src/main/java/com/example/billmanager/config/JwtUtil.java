package com.example.billmanager.config;

import com.example.billmanager.entity.User;
import com.example.billmanager.enums.UserIdentity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;

    @Getter
    private final long expireMinutes;

    public JwtUtil(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expire-minutes:120}") long expireMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMinutes = expireMinutes;
    }

    public String createToken(User user) {
        Date now = new Date();
        Date expiration = Date.from(now.toInstant().plus(Duration.ofMinutes(expireMinutes)));

        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .claim("account", user.getUserName() )
                .claim("identity", user.getIdentity().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }

    public String getAccount(String token) {
        Claims claims = parseToken(token);
        return claims.get("account", String.class);
    }

    public UserIdentity getIdentity(String token) {
        Claims claims = parseToken(token);
        String identity = claims.get("identity", String.class);
        return UserIdentity.valueOf(identity);
    }
}
