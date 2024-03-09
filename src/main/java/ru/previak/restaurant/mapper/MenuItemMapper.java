package ru.previak.restaurant.mapper;

import org.mapstruct.Mapper;
import ru.previak.restaurant.dto.MenuItemDTO;
import ru.previak.restaurant.entities.MenuItemEntity;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    MenuItemDTO menuItemEntity2MenuItemDTO(MenuItemEntity menuItemEntity);
    MenuItemEntity menuItemDTO2MenuItemEntity (MenuItemDTO menuItemDTO);
}
