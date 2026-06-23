package com.dimidev.cloudfilestorage.service;

import com.dimidev.cloudfilestorage.model.User;
import com.dimidev.cloudfilestorage.repository.UserRepository;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = repository.findByUsername(username)
                .orElseThrow(
                        () -> new RuntimeException(username)
                );

        return new CustomUserDetails(user);
    }
}