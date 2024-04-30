package com.muralis.minhasfinancas.service.impl;

import com.muralis.minhasfinancas.model.entity.User;
import com.muralis.minhasfinancas.model.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User findUser = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao cadastrado."));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(findUser.getEmail())
                .password(findUser.getPassword())
                .roles("USER")
                .build();
    }

}
