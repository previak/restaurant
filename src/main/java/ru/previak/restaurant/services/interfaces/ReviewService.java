package ru.previak.restaurant.services.interfaces;

import ru.previak.restaurant.dto.ReviewDTO;

public interface ReviewService {
    void postReview(ReviewDTO reviewDTO, Long userId);
}
