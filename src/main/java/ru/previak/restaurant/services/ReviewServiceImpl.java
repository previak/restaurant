package ru.previak.restaurant.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.previak.restaurant.dto.ReviewDTO;
import ru.previak.restaurant.entities.OrderEntity;
import ru.previak.restaurant.entities.OrderStatus;
import ru.previak.restaurant.entities.ReviewEntity;
import ru.previak.restaurant.exceptions.BadRequestException;
import ru.previak.restaurant.mapper.ReviewMapper;
import ru.previak.restaurant.repositories.OrderRepository;
import ru.previak.restaurant.repositories.ReviewRepository;
import ru.previak.restaurant.services.interfaces.ReviewService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class ReviewServiceImpl implements ReviewService {
    OrderRepository orderRepository;
    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    @Override
    public void postReview(ReviewDTO reviewDTO, Long userId) {
        Stream<OrderEntity> paidOrders = Optional.ofNullable(orderRepository.streamAllByUserId(userId))
                .orElseThrow(() -> new BadRequestException("You have no orders yet"))
                .filter(order -> order.getStatus() == OrderStatus.PAYED);

        boolean hasOrderedDish = paidOrders
                .flatMap(order -> order.getDishes().keySet().stream())
                .anyMatch(dish -> dish.equals(reviewDTO.getDishName()));

        if (!hasOrderedDish) {
            throw new BadRequestException("You did not order " + reviewDTO.getDishName() + " yet or the order is not payed");
        }
        reviewRepository.save(reviewMapper.reviewDTO2ReviewEntity(reviewDTO));
    }
    @Override
    public Map<String, Double> getAllDishesWithRating() {
        List<ReviewEntity> reviews = reviewRepository.findAll();

        return reviews.stream()
                .collect(Collectors.groupingBy(ReviewEntity::getDishName,
                        Collectors.averagingDouble(ReviewEntity::getRating)));
    }
    @Override
    public Map<String, Double> getTop3PopularDishes() {
        Stream<ReviewEntity> reviews = reviewRepository.streamAllBy();

        Map<String, Double> averageRatings = reviews
                .collect(Collectors.groupingBy(ReviewEntity::getDishName,
                        Collectors.averagingDouble(ReviewEntity::getRating)));

        return averageRatings.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
