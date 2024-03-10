package ru.previak.restaurant.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.previak.restaurant.entities.OrderStatus;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO {

    @NotNull(message = "Order id should not be null")
    Long id;

    @NotNull(message = "User id should not be null")
    Long userId;

    Date startDate;

    Date endDate;

    Map<String, Long> dishes;

    @NotNull(message = "Order status should not be null")
    OrderStatus status;
}
