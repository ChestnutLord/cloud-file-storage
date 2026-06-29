package com.dimidev.cloudfilestorage.qafordevs.it;

import com.dimidev.cloudfilestorage.dto.user.UserAuthDto;
import com.dimidev.cloudfilestorage.dto.user.UserUpsertDto;
import com.dimidev.cloudfilestorage.repository.UserRepository;
import com.dimidev.cloudfilestorage.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class itAuthControllerTests extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Успешная регистрация нового пользователя возвращает 200 OK")
    void givenValidDto_whenRegister_thenSuccessResponseAndUserSaved() throws Exception {
        // given
        UserUpsertDto validDto = new UserUpsertDto("dmitry", "password", "password");

        // when
        ResultActions result = mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)));

        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dmitry"));

        assertThat(userRepository.findByUsername("dmitry")).isPresent();
    }

    @Test
    @DisplayName("Ошибка валидации: пустой username возвращает 400 Bad Request")
    void givenEmptyUsername_whenRegister_thenReturnsBadRequest() throws Exception {
        // given
        UserUpsertDto invalidDto = new UserUpsertDto("", "password", "password");

        // when
        ResultActions result = mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ошибка валидации: короткий username возвращает 400 Bad Request")
    void givenSmallUsername_whenRegister_thenReturnsBadRequest() throws Exception {
        // given
        UserUpsertDto invalidDto = new UserUpsertDto("a", "password", "password");

        // when
        ResultActions result = mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Бизнес-ошибка: регистрация с уже существующим username возвращает ошибку 409")
    void givenExistingUsername_whenRegister_thenReturnsError() throws Exception {
        // given
        UserUpsertDto validDto = new UserUpsertDto("dmitry", "password", "password");

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());
        assertThat(userRepository.findByUsername("dmitry")).isPresent();

        // when
        ResultActions result = mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)));

        // then
        result.andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Успешный логин ползователя")
    void givenValidDto_whenLogin_thenSuccessResponse() throws Exception {
        // given
        UserUpsertDto validDto = new UserUpsertDto("dmitry", "password", "password");
        userService.create(validDto);
        assertThat(userRepository.findByUsername("dmitry")).isPresent();

        //when
        ResultActions result = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)));

        //then
        result.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Ошибка валидации: пустой username возвращает 400 Bad Request")
    void givenEmptyUsername_whenLogin_thenReturnsBadRequest() throws Exception {
        // given
        UserAuthDto invalidDto = new UserAuthDto("", "password");

        // when
        ResultActions result = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Бизнес-ошибка: логин по несуществующему юзернейму возвращает 401 Unauthorized")
    void givenNonexistingUsername_whenLogin_thenReturnsError() throws Exception {
        // given
        UserAuthDto invalidDto = new UserAuthDto("unknown_user", "password");

        //when
        ResultActions result = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        //then
        result.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Бизнес-ошибка: логин с неверным паролем возвращает 401 Unauthorized")
    void givenInvalidPassword_whenLogin_thenReturnsError() throws Exception {
        // given
        UserUpsertDto validRegistrationDto = new UserUpsertDto("dmitry", "password", "password");
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isOk());

        assertThat(userRepository.findByUsername("dmitry")).isPresent();

        UserAuthDto invalidPasswordLoginDto = new UserAuthDto("dmitry", "wrong_password");

        // when
        ResultActions result = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPasswordLoginDto)));

        // then
        result.andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
