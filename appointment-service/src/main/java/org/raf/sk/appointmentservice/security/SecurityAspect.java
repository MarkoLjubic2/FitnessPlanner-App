package org.raf.sk.appointmentservice.security;

import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.raf.sk.appointmentservice.security.tokenService.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;

@Aspect
@Configuration
public class SecurityAspect {

    @Value("${oauth.jwt.secret}")
    private String jwtSecret;

    private final TokenService tokenService;
    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    public SecurityAspect(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Around("@annotation(org.raf.sk.appointmentservice.security.CheckSecurity)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //Get method signature
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        //Check for authorization parameter
        String token = null;
        for (int i = 0; i < methodSignature.getParameterNames().length; i++) {
            if (methodSignature.getParameterNames()[i].equals("jwt")) {
                //Check bearer schema
                if (joinPoint.getArgs()[i].toString().startsWith("Bearer")) {
                    token = joinPoint.getArgs()[i].toString().split(" ")[1];
                }
            }
        }

        //If token is not presents return UNAUTHORIZED response
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //Try to parse token
        Claims claims = tokenService.parseToken(token);
        //If fails return UNAUTHORIZED response
        if (claims == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //Check user role and proceed if user has appropriate role for specified route
        CheckSecurity checkSecurity = method.getAnnotation(CheckSecurity.class);
        String role = claims.get("role", String.class);
        if (Arrays.asList(checkSecurity.roles()).contains(role)) {
            return joinPoint.proceed();
        }
        //Return FORBIDDEN if user hasn't appropriate role for specified route
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
