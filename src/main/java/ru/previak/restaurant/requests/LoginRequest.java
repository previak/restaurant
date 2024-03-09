package ru.previak.restaurant.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotNull(message = "Username should not be null")
    @NotBlank(message = "Username should not be blank")
    String username;

    @NotNull(message = "Password should not be null")
    @NotBlank(message = "Password should not be blank")
    String password;
}
