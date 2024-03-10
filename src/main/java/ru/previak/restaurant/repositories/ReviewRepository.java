package ru.previak.restaurant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.previak.restaurant.entities.ReviewEntity;

import java.util.stream.Stream;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Stream<ReviewEntity> streamAllBy();
}
