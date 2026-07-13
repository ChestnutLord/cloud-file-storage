package com.dimidev.cloudfilestorage.service;

import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.dto.user.UserUpsertDto;
import com.dimidev.cloudfilestorage.exception.DuplicateResourceException;
import com.dimidev.cloudfilestorage.mapper.UserMapper;
import com.dimidev.cloudfilestorage.model.User;
import com.dimidev.cloudfilestorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserReadDto create(UserUpsertDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new DuplicateResourceException("Пользователь с таким username уже существует");
        }
        User user = userMapper.toModel(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        return userMapper.toDto(userRepository.save(user)); //ToDo проверить что дата выставляется когда ткаим образом возвращаешь
    }
}