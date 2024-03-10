package ru.previak.restaurant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.previak.restaurant.entities.MenuItemEntity;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    Stream<MenuItemEntity> streamAllBy();
    Optional<MenuItemEntity> findByDishName(String dishName);
}
