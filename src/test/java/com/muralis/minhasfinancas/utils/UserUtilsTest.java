package com.muralis.minhasfinancas.utils;

import com.muralis.minhasfinancas.MinhasFinancasApplication;
import com.muralis.minhasfinancas.api.dto.responses.ResponseUserDTO;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.utils.impl.UserUtilsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest(classes = MinhasFinancasApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserUtilsTest {

    @SpyBean
    UserUtilsImpl utils;

    @Test
    @DisplayName("Deve ser possível converter uma entidate usuário para uma resposta de requisição.")
    void shouldBeAbleToConvertUserToResponseDTO () {
        User user = User.builder()
                .id(1L)
                .name("user")
                .build();

        User wrongUser = User.builder().build();

        ResponseUserDTO userResponseDto = utils.convertUserToResponseUserDto(user);

        Assertions.assertThat(userResponseDto.getId().equals(user.getId()));
        Assertions.assertThat(userResponseDto.getNome().equals(user.getName()));

        Throwable error = catchThrowable( () -> utils.convertUserToResponseUserDto(wrongUser) );
        assertThat(error).isNull();
    }

}
