package ru.previak.restaurant.mapper;

import org.mapstruct.Mapper;
import ru.previak.restaurant.dto.DishDTO;
import ru.previak.restaurant.entities.DishEntity;

@Mapper(componentModel = "spring")
public interface DishMapper {
    DishDTO dishEntity2DishDTO (DishEntity dishEntity);
    DishEntity dishDTO2DishEntity (DishDTO dishDTO);
}
