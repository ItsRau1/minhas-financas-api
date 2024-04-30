package com.muralis.minhasfinancas.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muralis.minhasfinancas.api.dto.UserDTO;
import com.muralis.minhasfinancas.exception.AuthenticateError;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.service.EntryService;
import com.muralis.minhasfinancas.service.UserService;
import com.muralis.minhasfinancas.service.impl.JwtServiceImpl;
import com.muralis.minhasfinancas.service.impl.SecurityUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserControllerTest {

    static final String API = "/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService service;

    @MockBean
    JwtServiceImpl jwtService;

    @MockBean
    SecurityUserDetailsService securityUserDetailsService;

    @MockBean
    EntryService entryService;

    @Test
    @DisplayName("Deve ser possível se autenticar.")
    public void shouldBeAbleToAuthenticate () throws Exception {
        String email = "user@email.com";
        String password = "secret";
        UserDTO dto = UserDTO.builder().email(email).senha(password).build();
        User user = User.builder().id(1L).email(email).password(password).build();
        Mockito.when(service.authenticate(email, password)).thenReturn(user);
        String json = new ObjectMapper().writeValueAsString(dto);

         MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

         mvc.perform(request)
                 .andExpect(MockMvcResultMatchers.status().isOk())
                 .andExpect(MockMvcResultMatchers.jsonPath("nome").value(user.getName()));
    }

    @Test
    @DisplayName("Não deve ser possível se autenticar com erro.")
    public void notShouldBeAbleToAuthenticateWithError() throws Exception {
        String email = "user@email.com";
        String password = "secret";
        UserDTO dto = UserDTO.builder().email(email).senha(password).build();
        Mockito.when( service.authenticate(email, password) ).thenThrow(AuthenticateError.class);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve ser possível registrar um usuário.")
    public void shouldBeAbleToRegisterAUser() throws Exception {
        String name = "user";
        String email = "user@email.com";
        String password = "secret";
        UserDTO dto = UserDTO.builder().nome(name).email(email).senha(password).build();
        User user = User.builder().id(1L).name(name).email(email).password(password).build();

        Mockito.when(service.registerUser(Mockito.any(User.class))).thenReturn(user);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(user.getName()));
    }

    @Test
    @DisplayName("Não deve ser possível registrar um usuário com erro.")
    public void notShouldBeAbleToRegisterAUSerWithError () throws Exception {
        UserDTO dto = UserDTO.builder().email("user@email.com").senha("secret").build();

        Mockito.when( service.registerUser((Mockito.any(User.class)))).thenThrow(BusinessRuleException.class);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post( API  )
                .accept( JSON )
                .contentType( JSON )
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Não deve ser possível obter o saldo de um usuário inexistente.")
    public void notShouldBeAbleGetBalanceWhenUserNotExist () throws Exception {
        Mockito.when(service.getById(1L)).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get( API.concat("/1/saldo")  )
                .accept( JSON )
                .contentType( JSON );

        mvc.perform(request)
                .andExpect( MockMvcResultMatchers.status().isNotFound() );
    }

}
