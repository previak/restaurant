package ru.previak.restaurant.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.previak.restaurant.services.interfaces.RestaurantStatisticsService;
import ru.previak.restaurant.services.interfaces.ReviewService;

import java.util.Date;
import java.util.Map;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Validated
@RequestMapping("/api/statistics")
@Api("Restaurant statistics controller")
public class RestaurantStatisticsController {
    RestaurantStatisticsService restaurantStatisticsService;
    ReviewService reviewService;

    @GetMapping("/revenue")
    @ApiOperation("Get total revenue")
    public ResponseEntity<String> getTotalRevenue() {
        return ResponseEntity.ok("Total revenue: " + restaurantStatisticsService.getTotalRevenue());
    }

    @GetMapping("/orders-amount")
    @ApiOperation("Get amount of payed orders")
    public ResponseEntity<String> getAmountOfPayedOrders() {
        return ResponseEntity.ok("Amount of payed orders: " + restaurantStatisticsService.getAmountOfPayedOrders());
    }

    @GetMapping("/reviews")
    @ApiOperation("Get dishes with their rating")
    public ResponseEntity<Map<String, Double>> getAllDishesWithRating() {
        return ResponseEntity.ok(reviewService.getAllDishesWithRating());
    }

    @GetMapping("/reviews/top")
    @ApiOperation("Get top 3 dishes with their rating")
    public ResponseEntity<Map<String, Double>> getTop3PopularDishes() {
        return ResponseEntity.ok(reviewService.getTop3PopularDishes());
    }

    @GetMapping("/orders-in-period")
    @ApiOperation("Get amount of orders in selected period")
    public ResponseEntity<Long> getOrderCountBetweenDates(
            @RequestParam Date startDate,
            @RequestParam Date endDate
    ) {
        return ResponseEntity.ok(restaurantStatisticsService.getOrderCountBetweenDates(startDate, endDate));
    }
}
