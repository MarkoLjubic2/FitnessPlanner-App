package org.raf.sk.userservice.security.tokenService;

import io.jsonwebtoken.Claims;

public interface TokenService {

    String generate(Claims claims);

    Claims parseToken(String jwt);

    boolean isTokenValid(String jwt);

    String getRole(String jwt);

}
