package com.muralis.minhasfinancas.service;

import com.muralis.minhasfinancas.MinhasFinancasApplication;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SpringBootTest(classes = MinhasFinancasApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class JwtServiceTest {

    @SpyBean
    JwtServiceImpl service;

    @Test
    @DisplayName("Deve ser possível criar um novo token JWT.")
    void shouldBeAbleToCreateANewToken () {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password("secret")
                .build();

        String token = service.newToken(user);

        Assertions.assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Deve ser possível obter as 'Claims'.")
    void shouldBeAbleToGetClaim () {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password("secret")
                .build();

        String token = service.newToken(user);

        Claims claims = service.getClaims(token);

        Assertions.assertThat(claims.get("name").equals(user.getName()));
        Assertions.assertThat(claims.get("userId").equals(user.getId()));
    }

    @Test
    @DisplayName("Deve ser possível verificar se um token JWT é válido.")
    void shouldBeAbleToValidToken () {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password("secret")
                .build();

        String token = service.newToken(user);
        Boolean isValid = service.isValidToken(token);

        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Deve ser possível obter um usuário logado.")
    void shouldBeAbleToGetLoginUser () {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password("secret")
                .build();

        String token = service.newToken(user);
        String loginUser = service.getLoginUser(token);

        Assertions.assertThat(loginUser.equals(user.getEmail()));
    }

}

