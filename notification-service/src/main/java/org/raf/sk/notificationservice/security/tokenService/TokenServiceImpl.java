package org.raf.sk.notificationservice.security.tokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${oauth.jwt.secret}")
    private String jwtSecret;

    @Override
    public String generate(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Override
    public Claims parseToken(String jwt) {
        jwt = jwt.replaceAll("Bearer ", "");
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
        return claims;
    }

    @Override
    public String getRoleFromToken(String jwt) {
        Claims claims = parseToken(jwt);
        if (claims == null) return null;
        return claims.get("role", String.class);
    }

    @Override
    public String getMailFromToken(String jwt) {
        Claims claims = parseToken(jwt);
        if (claims == null) return null;
        return claims.getSubject();
    }

}
