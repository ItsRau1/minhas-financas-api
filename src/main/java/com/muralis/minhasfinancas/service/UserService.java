package com.muralis.minhasfinancas.service;

import com.muralis.minhasfinancas.model.entity.User;

import java.util.Optional;

public interface UserService {

	User authenticate(String email, String password);
	
	User registerUser(User user);
	
	void validEmail(String email);

	Optional<User> getById (Long id);
	
}
