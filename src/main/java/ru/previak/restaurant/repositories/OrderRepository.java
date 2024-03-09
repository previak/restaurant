package ru.previak.restaurant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.previak.restaurant.entities.OrderEntity;

import java.util.stream.Stream;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Stream<OrderEntity> streamAllByUserId(Long userId);
    Stream<OrderEntity> streamAllBy();
}
