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
public class DishDTO {

    @NotNull(message = "Dish name should not be null")
    @NotBlank(message = "Dish name should not be blank")
    String name;

    @NotNull(message = "Dish price should not be null")
    @Min(value = 0, message = "Dish price should not be lower than 0")
    Double price;

    @NotNull(message = "Dish cooking time in minutes should not be null")
    @Min(value = 0, message = "Dish cooking time in minutes should not be less than 0")
    Long cookingTimeInMinutes;
}
