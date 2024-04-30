package com.muralis.minhasfinancas.service.impl;

import com.muralis.minhasfinancas.exception.AuthenticateError;
import com.muralis.minhasfinancas.exception.BusinessRuleException;
import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.model.repository.UserRepository;
import com.muralis.minhasfinancas.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserRepository repository;
	private final PasswordEncoder encoder;

	public UserServiceImpl(UserRepository repository, PasswordEncoder encoder) {
		this.repository = repository;
		this.encoder = encoder;
    }

	@Override
	public User authenticate(String email, String password) {
		Optional<User> user = repository.findByEmail(email);

		if(!user.isPresent()) {
			throw new AuthenticateError("Usuário ou senha incorreto.");
		}

		boolean correctPasswords = encoder.matches(password, user.get().getPassword());

		if(!correctPasswords) {
			throw new AuthenticateError("Usuário ou senha incorreto.");
		}

		return user.get();
	}

	@Override
	@Transactional
	public User registerUser(User user) {
		validEmail(user.getEmail());
		criptoPassword(user);
		return repository.save(user);
	}

	@Override
	public void validEmail(String email) {
		if (repository.existsByEmail(email)) {
			throw new BusinessRuleException("Email já cadastrado.");
		}
	}

	@Override
	public Optional<User> getById(Long id) {
		return repository.findById(id);
	}

	private void criptoPassword (User user) {
		String password = user.getPassword();
		String encriptPassword = encoder.encode(password);
		user.setPassword(encriptPassword);
	}

}
