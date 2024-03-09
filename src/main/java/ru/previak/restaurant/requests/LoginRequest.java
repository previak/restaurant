package ru.previak.restaurant.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.previak.restaurant.entities.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @NonNull
    String username;

    @NonNull
    String password;
}
