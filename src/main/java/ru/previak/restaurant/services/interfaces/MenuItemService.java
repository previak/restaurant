package ru.previak.restaurant.services.interfaces;

import ru.previak.restaurant.dto.MenuItemDTO;

import java.util.List;

public interface MenuItemService {
    void createMenuItem(MenuItemDTO menuItemDTO);
    void updateMenuItem(String dishName, MenuItemDTO menuItemDTO);
    List<MenuItemDTO> getAllMenuItems();
}
