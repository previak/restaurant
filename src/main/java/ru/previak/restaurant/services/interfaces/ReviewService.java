package ru.previak.restaurant.services.interfaces;

import ru.previak.restaurant.dto.ReviewDTO;

import java.util.Map;

public interface ReviewService {
    void postReview(ReviewDTO reviewDTO, Long userId);
    Map<String, Double> getAllDishesWithRating();
    Map<String, Double> getTop3PopularDishes();
}
