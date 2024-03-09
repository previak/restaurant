package ru.previak.restaurant.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.previak.restaurant.dto.ReviewDTO;
import ru.previak.restaurant.entities.OrderEntity;
import ru.previak.restaurant.entities.ReviewEntity;
import ru.previak.restaurant.exceptions.BadRequestException;
import ru.previak.restaurant.mapper.DishMapper;
import ru.previak.restaurant.mapper.ReviewMapper;
import ru.previak.restaurant.repositories.OrderRepository;
import ru.previak.restaurant.repositories.ReviewRepository;
import ru.previak.restaurant.services.interfaces.ReviewService;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class ReviewServiceImpl implements ReviewService {
    OrderRepository orderRepository;
    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    DishMapper dishMapper;
    @Override
    public void postReview(ReviewDTO reviewDTO, Long userId) {
        Stream<OrderEntity> orders = Optional.ofNullable(orderRepository.streamAllByUserId(userId))
                .orElseThrow(() -> new BadRequestException("You have no orders yet"));

        boolean hasOrderedDish = orders
                .flatMap(order -> order.getDishes().keySet().stream())
                .anyMatch(dish -> dish.equals(reviewDTO.getDishName()));

        if (!hasOrderedDish) {
            throw new BadRequestException("You did not order " + reviewDTO.getDishName() + " yet");
        }
        ReviewEntity reviewEntity = reviewMapper.reviewDTO2ReviewEntity(reviewDTO);
        reviewRepository.save(reviewEntity);
    }
}
