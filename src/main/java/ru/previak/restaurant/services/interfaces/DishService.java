package ru.previak.restaurant.services.interfaces;

import ru.previak.restaurant.dto.DishDTO;

import java.util.List;

public interface DishService {
    void createDish(DishDTO dishDTO);
    void removeDish(String dishName);
    void updateDish(String dishName, DishDTO dishDTO);
    List<DishDTO> getAllDishes();
}
