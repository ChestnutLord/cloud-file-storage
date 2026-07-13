package com.dimidev.cloudfilestorage.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpsertDto(
        @NotBlank(message = "Поле username обязательно к заполнению")
        @Size(
                min = 5,
                max = 20,
                message = "Длина username должна быть от 5 до 20 символов"
        )
        String username,

        @NotBlank(message = "Поле password обязательно к заполнению")
        @Size(
                min = 5,
                max = 20,
                message = "Длина пароля должна быть от 5 до 20 символов"
        )
        String password) {
}
