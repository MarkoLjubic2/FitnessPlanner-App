package org.raf.sk.appointmentservice.security.tokenService;

import io.jsonwebtoken.Claims;

public interface TokenService {

    String generate(Claims claims);

    Claims parseToken(String jwt);

    Long getUserId(String jwt);

    String getRole(String jwt);

}
