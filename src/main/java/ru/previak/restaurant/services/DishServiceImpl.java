package ru.previak.restaurant.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.previak.restaurant.dto.DishDTO;
import ru.previak.restaurant.entities.DishEntity;
import ru.previak.restaurant.exceptions.NotFoundException;
import ru.previak.restaurant.mapper.DishMapper;
import ru.previak.restaurant.repositories.DishRepository;
import ru.previak.restaurant.services.interfaces.DishService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class DishServiceImpl implements DishService {
    DishRepository dishRepository;
    DishMapper dishMapper;
    @Override
    public void createDish(DishDTO dishDTO) {
        DishEntity dishEntity = dishMapper.dishDTO2DishEntity(dishDTO);
        dishRepository.save(dishEntity);
    }

    @Override
    public void removeDish(String dishName) {
        dishRepository.removeByName(dishName);
    }

    @Override
    public void updateDish(String dishName, DishDTO dishDTO) {
        DishEntity dishEntity = dishRepository.findByName(dishName)
                .orElseThrow(() -> new NotFoundException("Dish with name " + dishName + " not found"));

        DishEntity updatedDishEntity = dishMapper.dishDTO2DishEntity(dishDTO);
        dishEntity.setName(updatedDishEntity.getName());
        dishEntity.setPrice(updatedDishEntity.getPrice());
        dishEntity.setCookingTimeInMinutes(updatedDishEntity.getCookingTimeInMinutes());

        dishRepository.save(dishEntity);
    }

    @Override
    public List<DishDTO> getAllDishes() {
        Stream<DishEntity> allDishEntities = dishRepository.streamAllBy();
        return allDishEntities
                .map(dishMapper::dishEntity2DishDTO)
                .collect(Collectors.toList());
    }
}
