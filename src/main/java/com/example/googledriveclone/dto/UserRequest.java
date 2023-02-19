package com.example.googledriveclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotEmpty(message = "username should not be empty")
    @Size(min = 2, max = 15, message = "Username should be between 2 and 15")
    private String username;
    @NotEmpty(message = "username should not be empty")
    private String password;
}
