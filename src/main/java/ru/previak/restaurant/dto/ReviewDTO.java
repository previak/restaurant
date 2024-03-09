package ru.previak.restaurant.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDTO {

    @NotBlank(message = "Dish name should not be blank")
    @NotNull(message = "Dish name should not be null")
    String dishName;

    @NotNull(message = "Rating should not be null")
    @Min(value = 1, message = "Rating should be at least 1")
    @Max(value = 5, message = "Rating should be maximum 5")
    Integer rating;

    @NotBlank(message = "Comment should not be blank")
    @NotNull(message = "Comment should not be null")
    String comment;

}
