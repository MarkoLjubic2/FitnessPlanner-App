package org.raf.sk.appointmentservice.security.tokenService;

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
    public Long getUserId(String jwt) {
        jwt = jwt.replaceAll("Bearer ", "");
        Claims claims = parseToken(jwt);
        if (claims != null) {
            Number id = (Number) claims.get("id");
            return id.longValue();
        }
        return null;
    }

    @Override
    public String getRole(String jwt) {
        jwt = jwt.replaceAll("Bearer ", "");
        Claims claims = parseToken(jwt);
        return (claims != null) ? (String) claims.get("role") : null;
    }

}
