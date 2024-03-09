package ru.previak.restaurant.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.previak.restaurant.dto.OrderDTO;
import ru.previak.restaurant.entities.UserEntity;
import ru.previak.restaurant.services.interfaces.OrderService;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@RequestMapping("/api/orders")
@Api("Order controller")
public class OrderController {

    OrderService orderService;
    UserDetailsService userDetailsService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation("Create order")
    public ResponseEntity<String> createOrder(
            Principal principal
    ) {
        UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(principal.getName());
        Long orderId = orderService.createOrder(user.getId());
        return ResponseEntity.ok("Your order's id = " + orderId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{orderId}")
    @ApiOperation("Add dish to order")
    public ResponseEntity<String> addDishToOrder(
            @PathVariable Long orderId,
            @RequestParam String dishName,
            @RequestParam Long amount,
            Principal principal
    ) {
        UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(principal.getName());
        orderService.addDishToOrder(orderId, dishName, amount, user.getId());
        return ResponseEntity.ok("Dish was successfully added to order with id = " + orderId);
    }

    @GetMapping
    @ApiOperation("Get user's orders")
    ResponseEntity<List<OrderDTO>> getUserOrders(
            Principal principal
    ) {
        UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(principal.getName());
        return ResponseEntity.ok(orderService.getUserOrders(user.getId()));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{orderId}")
    @ApiOperation("Submit a cooking order")
    public ResponseEntity<String> startOrderCooking(
            @PathVariable Long orderId,
            Principal principal
    ) {
        UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(principal.getName());
        orderService.submitCookingOrder(orderId, user.getId());
        return ResponseEntity.ok("Order with id " + orderId + " has started cooking");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    @ApiOperation("Cancel the cooking order")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long orderId,
            Principal principal
    ) {
        UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(principal.getName());
        orderService.cancelOrder(orderId, user.getId());
        return ResponseEntity.ok("Order with id " + orderId + " has been canceled");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{orderId}/pay")
    @ApiOperation("Pay the order")
    public ResponseEntity<String> payOrder(
            @PathVariable Long orderId,
            Principal principal
    ) {
        UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(principal.getName());
        orderService.payOrder(orderId, user.getId());
        return ResponseEntity.ok("Order with id " + orderId + " has been paid for");
    }
}
