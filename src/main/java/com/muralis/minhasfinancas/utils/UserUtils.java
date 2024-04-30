package com.muralis.minhasfinancas.utils;

import com.muralis.minhasfinancas.api.dto.UserDTO;
import com.muralis.minhasfinancas.api.dto.responses.ResponseUserDTO;
import com.muralis.minhasfinancas.model.entity.User;

public interface UserUtils {

    ResponseUserDTO convertUserToResponseUserDto (User user);

    User convertDtoToEntity (UserDTO dto);

}
