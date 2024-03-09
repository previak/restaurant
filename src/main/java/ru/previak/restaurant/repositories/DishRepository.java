package ru.previak.restaurant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.previak.restaurant.entities.DishEntity;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface DishRepository extends JpaRepository<DishEntity, Long> {
    void removeByName(String name);
    Optional<DishEntity> findByName(String name);
    Stream<DishEntity> streamAllBy();
}
