package com.muralis.minhasfinancas.service;

import com.muralis.minhasfinancas.MinhasFinancasApplication;
import com.muralis.minhasfinancas.exception.AuthenticateError;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.model.repository.UserRepository;
import com.muralis.minhasfinancas.service.impl.UserServiceImpl;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@SpringBootTest(classes = MinhasFinancasApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @SpyBean
    UserServiceImpl service;

    @MockBean
    UserRepository repository;

    @Test
    @DisplayName("Deve ser possível registrar um novo usuário.")
    void shouldBeAbleRegisterUser () {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Mockito.doNothing().when(service).validEmail(Mockito.anyString());
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password("secret")
                .build();
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);

        User userSave = service.registerUser(user);

        assertThat(userSave).isNotNull();
        assertThat(userSave.getName()).isEqualTo(user.getName());
        boolean correctPassword = encoder.matches("secret", userSave.getPassword());
        assertThat(correctPassword).isTrue();
    }

    @Test
    @DisplayName("Não deve ser possível registrar um novo usuário com um email já registrado.")
    void shouldBeAbleNotRegisterUserWithEmailAlreadyExist () {
        org.junit.jupiter.api.Assertions.assertThrows(BusinessRuleException.class, () -> {
            User user = User.builder().email("user@email.com").build();
            Mockito.doThrow(BusinessRuleException.class).when(service).validEmail(user.getEmail());

            service.registerUser(user);
            Mockito.verify( repository, Mockito.never()).save(user);
        });
    }

    @Test
    @DisplayName("Deve ser possível autenticar um usuário.")
    void shouldBeAbleAuthenticateUser () {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String email = "user@email.com";
        String password = encoder.encode("secret");

        User user = User.builder().email(email).password(password).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        User res = service.authenticate(email, "secret");
        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("Não deve ser possível se autenticar com o email incorreto.")
    void shouldBeAbleToLaunchErrorWhenNotFoundUserWithEmailInformed() {
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> service.authenticate("user@email.com", "secret") );
        assertThat(exception).isInstanceOf(AuthenticateError.class).hasMessage("Usuário ou senha incorreto.");
    }

    @Test
    @DisplayName("Não deve ser possível se autenticar com a senha incorreta.")
    void shouldBeAbleToLaunchErrorWhenFoundUserButWrongPassword() {
        String password = "secret";
        User user = User.builder().email("user@email.com").password(password).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));

        Throwable exception = catchThrowable(() -> service.authenticate(user.getEmail(), "wrongPassword") );
        assertThat(exception).isInstanceOf(AuthenticateError.class).hasMessage("Usuário ou senha incorreto.");
    }

    @Test
    @DisplayName("Deve ser possível válidar um email.")
    void shouldBeAbleValidEmail () {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
            service.validEmail("user@email.com");
        });
    }

    @Test
    @DisplayName("Não deve ser possível válidar um email já existente.")
    void shouldBeAbleLaunchErrorWhenEmailAlreadyExist() {
        org.junit.jupiter.api.Assertions.assertThrows(BusinessRuleException.class, () -> {
            Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
            service.validEmail("user@email.com");
        });
    }

}
