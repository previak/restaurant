package ru.previak.restaurant.services.interfaces;

import ru.previak.restaurant.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    Long createOrder(Long userId);
    void addDishToOrder(Long orderId, String dishName, Long amount, Long userId);
    List<OrderDTO> getUserOrders(Long userId);
    void submitCookingOrder(Long orderId, Long userId);
    void cancelOrder(Long orderId, Long userId);
    void payOrder(Long orderId, Long userId);
}
