package ru.previak.restaurant.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.previak.restaurant.dto.ReviewDTO;
import ru.previak.restaurant.entities.UserEntity;
import ru.previak.restaurant.services.interfaces.ReviewService;

import javax.validation.Valid;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@RequestMapping("/api/reviews")
@Api("Reviews controller")
public class ReviewController {

    UserDetailsService userDetailsService;
    ReviewService reviewService;

    @PostMapping
    @ApiOperation("Post review")
    public ResponseEntity<String> postReview(
            @Valid @RequestBody ReviewDTO reviewDTO,
            Principal principal
    ) {
        UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(principal.getName());
        reviewService.postReview(reviewDTO, user.getId());
        return ResponseEntity.ok("Review was successfully posted");
    }
}
