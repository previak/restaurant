package ru.previak.restaurant.mapper;

import org.mapstruct.Mapper;
import ru.previak.restaurant.dto.DishDTO;
import ru.previak.restaurant.dto.OrderDTO;
import ru.previak.restaurant.entities.DishEntity;
import ru.previak.restaurant.entities.OrderEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO orderEntity2OrderDTO (OrderEntity orderEntity);
    OrderEntity orderDTO2OrderEntity (OrderDTO orderDTO);
}
