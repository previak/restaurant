package ru.previak.restaurant.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.previak.restaurant.store.entities.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
