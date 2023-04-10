package com.example.googledriveclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto  {
    @NotEmpty(message = "Имя пользователя не должен быть пустым")
    @Size(min = 3, max = 30, message = "Имя пользователя должно содержать от 3 до 30 символов")
    private String username;

    @NotEmpty(message = "Пароль не должен быть пустым")
    @Size(min = 8, max = 50, message = "Пароль должно содержать минимум 8 символов")
    private String password;

    @NotEmpty(message = "Пароль не должен быть пустым")
    @Size(min = 8, max = 50, message = "Пароль должно содержать минимум 8 символов")
    private String passwordConfirmation;
}
