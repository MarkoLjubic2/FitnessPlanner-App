package org.raf.sk.notificationservice.security.tokenService;

import io.jsonwebtoken.Claims;

public interface TokenService {

    String generate(Claims claims);

    Claims parseToken(String jwt);

    String getMailFromToken(String jwt);

}
