package ru.previak.restaurant.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemDTO {

    @NotNull(message = "Dish name should not be null")
    @NotBlank(message = "Dish name should not be blank")
    String dishName;

    @NotNull(message = "Amount of dishes should not be null")
    @Min(value = 0, message = "Amount of dishes should not be less than 0")
    Long amount;
}
