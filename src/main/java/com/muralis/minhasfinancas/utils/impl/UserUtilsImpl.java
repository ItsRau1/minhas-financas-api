package com.muralis.minhasfinancas.utils.impl;

import com.muralis.minhasfinancas.api.dto.UserDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseUserDTO;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.utils.UserUtils;
import org.springframework.stereotype.Service;

@Service
public class UserUtilsImpl implements UserUtils {

    @Override
    public ResponseUserDTO convertUserToResponseUserDto(User user) {
        return ResponseUserDTO.builder()
                .id(user.getId())
                .nome(user.getName())
                .build();
    }

    @Override
    public User convertDtoToEntity(UserDTO dto) {
        if (dto.getNome() == null) {
            throw new BusinessRuleException("Insira um nome para o usuário");
        }
        return User.builder()
                .name(dto.getNome())
                .email(dto.getEmail())
                .password(dto.getSenha())
                .build();
    }

}
