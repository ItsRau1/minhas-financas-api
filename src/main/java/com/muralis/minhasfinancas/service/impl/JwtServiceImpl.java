package com.muralis.minhasfinancas.service.impl;

import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.key-signature}")
    private String keySignature;

    @Override
    public String newToken(User user) {
        long expirationMinutes = Long.parseLong(expiration);
        LocalDateTime dateHourExpiration = LocalDateTime.now().plusMinutes(expirationMinutes);
        Instant instant = dateHourExpiration.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);

        String HourExpiratiooonToken = dateHourExpiration.toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        return Jwts.builder()
                .setExpiration(date)
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .claim("userId", user.getId())
                .claim("hourExpiration", HourExpiratiooonToken)
                .signWith(SignatureAlgorithm.HS512, keySignature)
                .compact();
    }

    @Override
    public Claims getClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(keySignature)
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            Claims claims = getClaims(token);
            Date dateExpToken = claims.getExpiration();

            LocalDateTime dataExpiracao = dateExpToken.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            boolean dataHoraAtualIsAfterDataExpiracao = LocalDateTime.now().isAfter(dataExpiracao);
            return !dataHoraAtualIsAfterDataExpiracao;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public String getLoginUser(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

}
