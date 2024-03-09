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
import org.springframework.web.bind.annotation.RestController;
import ru.previak.restaurant.services.interfaces.RestaurantStatisticsService;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Validated
@RequestMapping("/api/statistics")
@Api("Restaurant statistics controller")
public class RestaurantStatisticsController {
    RestaurantStatisticsService restaurantStatisticsService;

    @GetMapping()
    @ApiOperation("Get total revenue")
    public ResponseEntity<String> getTotalRevenue() {
        return ResponseEntity.ok("Total revenue: " + restaurantStatisticsService.getTotalRevenue());
    }


}
