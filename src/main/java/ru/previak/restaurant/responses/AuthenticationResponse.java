package ru.previak.restaurant.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
}
