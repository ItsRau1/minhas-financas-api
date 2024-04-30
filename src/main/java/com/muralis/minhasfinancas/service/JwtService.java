package com.muralis.minhasfinancas.service;

import com.muralis.minhasfinancas.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public interface JwtService {

    String newToken(User user);

    Claims getClaims(String token) throws ExpiredJwtException;

    boolean isValidToken(String token);

    String getLoginUser(String token);

}
