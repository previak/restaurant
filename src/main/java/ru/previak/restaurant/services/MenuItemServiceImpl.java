package ru.previak.restaurant.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.previak.restaurant.dto.MenuItemDTO;
import ru.previak.restaurant.entities.DishEntity;
import ru.previak.restaurant.entities.MenuItemEntity;
import ru.previak.restaurant.exceptions.NotFoundException;
import ru.previak.restaurant.mapper.MenuItemMapper;
import ru.previak.restaurant.repositories.DishRepository;
import ru.previak.restaurant.repositories.MenuItemRepository;
import ru.previak.restaurant.services.interfaces.MenuItemService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MenuItemServiceImpl implements MenuItemService {
    MenuItemRepository menuItemRepository;
    DishRepository dishRepository;
    MenuItemMapper menuItemMapper;
    @Override
    public void createMenuItem(MenuItemDTO menuItemDTO) {
        MenuItemEntity menuItemEntity = menuItemMapper.menuItemDTO2MenuItemEntity(menuItemDTO);
        menuItemRepository.save(menuItemEntity);
    }

    @Override
    public void updateMenuItem(String dishName, MenuItemDTO menuItemDTO) {
        DishEntity dishEntity = dishRepository.findByName(dishName)
                .orElseThrow(() -> new NotFoundException("Dish with name " + dishName + " not found"));

        MenuItemEntity menuItemEntity = menuItemRepository.findByDishName(dishEntity.getName())
                .orElseThrow(() -> new NotFoundException("MenuItem with name " + dishName + " not found"));

        MenuItemEntity updatedMenuEntity = menuItemMapper.menuItemDTO2MenuItemEntity(menuItemDTO);
        menuItemEntity.setAmount(updatedMenuEntity.getAmount());
        menuItemRepository.save(menuItemEntity);
    }

    @Override
    public List<MenuItemDTO> getAllMenuItems() {
        Stream<MenuItemEntity> allMenuItemEntities = menuItemRepository.streamAllBy();
        return allMenuItemEntities
                .map(menuItemMapper::menuItemEntity2MenuItemDTO)
                .collect(Collectors.toList());
    }
}
