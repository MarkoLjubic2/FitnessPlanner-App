package org.raf.sk.userservice.security.tokenService;

import io.jsonwebtoken.Claims;

public interface TokenService {

    String generate(Claims claims);

    Claims parseToken(String jwt);

    Long getUserId(String jwt);

    String getRole(String jwt);

    String getUsername(String jwt);

}
