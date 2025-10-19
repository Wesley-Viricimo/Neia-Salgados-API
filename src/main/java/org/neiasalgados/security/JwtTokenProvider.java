package org.neiasalgados.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtTokenProvider {
    @Value("${api.security.token.secret}")
    private String secretKey;
    private SecretKey key;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(UserSecurity userSecurity) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000);

        return Jwts.builder()
                .subject(userSecurity.getUsername())
                .claim("userId", userSecurity.getId())
                .claim("role", userSecurity.getAuthorities().iterator().next().getAuthority())
                .claim("isActive", userSecurity.getIsActive())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);
        Boolean isActive = claims.get("isActive", Boolean.class);

        UserSecurity userDetails = new UserSecurity(userId, email, role, isActive);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
