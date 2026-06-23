package com.dimidev.cloudfilestorage.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpsertDto(
        @NotBlank(message = "Поле username обязательно к заполнению")
        @Size(
                min = 2,
                max = 30,
                message = "Длина username должна быть от 2 до 30 символов"
        )
        String username,

        @NotBlank(message = "Поле password обязательно к заполнению")
        @Size(min = 5, message = "Минимальная длина пароля составляет 5 символов")
        String password,

        @NotBlank(message = "Поле password обязательно к заполнению")
        String matchingPassword) {
}
