package com.dimidev.cloudfilestorage.service;

import com.dimidev.cloudfilestorage.dto.user.UserAuthDto;
import com.dimidev.cloudfilestorage.dto.user.UserReadDto;
import com.dimidev.cloudfilestorage.dto.user.UserUpsertDto;
import com.dimidev.cloudfilestorage.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    @Transactional
    public UserReadDto registration(UserUpsertDto dto,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        UserReadDto user = userService.create(dto);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(securityContext);
        securityContextRepository.saveContext(securityContext, request, response);
        log.info("Пользователь зарегистрирован: username={}", user.username());
        return user;
    }

    public UserReadDto login(UserAuthDto dto,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(securityContext);
        securityContextRepository.saveContext(securityContext, request, response);
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        log.info("Пользователь вошёл в систему: username={}", principal.getUsername());
        return new UserReadDto(principal.getUsername()); // todo null pointer
    }
}