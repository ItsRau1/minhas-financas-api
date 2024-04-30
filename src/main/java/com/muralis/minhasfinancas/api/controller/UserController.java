package com.muralis.minhasfinancas.api.controller;

import com.muralis.minhasfinancas.api.dto.TokenDTO;
import com.muralis.minhasfinancas.api.dto.UserDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseUserDTO;
import com.muralis.minhasfinancas.exception.AuthenticateError;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.service.JwtService;
import com.muralis.minhasfinancas.service.EntryService;
import com.muralis.minhasfinancas.service.UserService;
import com.muralis.minhasfinancas.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UserController {
    private final UserService service;
    private final EntryService entryService;
    private final JwtService jwtService;
    private final UserUtils utils;

    public UserController(UserService service, EntryService entryService, JwtService jwtService, UserUtils utils) {
        this.service = service;
        this.entryService = entryService;
        this.jwtService = jwtService;
        this.utils = utils;
    }

    @PostMapping("/autenticar")
    public ResponseEntity<?> authenticate (@RequestBody UserDTO dto) {
        try {
            User userAuthenticated = service.authenticate(dto.getEmail(), dto.getSenha());
            String token = jwtService.newToken(userAuthenticated);
            TokenDTO tokenDTO = new TokenDTO(userAuthenticated.getId(), userAuthenticated.getName(), token);
            return ResponseEntity.status(HttpStatus.OK).body(tokenDTO);
        } catch (AuthenticateError e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> register (@RequestBody UserDTO dto) {
        try {
            User user = utils.convertDtoToEntity(dto);
            User userSave = service.registerUser(user);
            ResponseUserDTO response = utils.convertUserToResponseUserDto(userSave);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<?> getBalance (@PathVariable("id") Long id) {
            Optional<User> user = service.getById(id);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(entryService.getBalanceById(id));
    }

}
