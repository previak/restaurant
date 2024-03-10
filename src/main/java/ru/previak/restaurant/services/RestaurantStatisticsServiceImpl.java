package ru.previak.restaurant.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.previak.restaurant.entities.DishEntity;
import ru.previak.restaurant.entities.OrderEntity;
import ru.previak.restaurant.entities.OrderStatus;
import ru.previak.restaurant.exceptions.NotFoundException;
import ru.previak.restaurant.repositories.DishRepository;
import ru.previak.restaurant.repositories.OrderRepository;
import ru.previak.restaurant.services.interfaces.RestaurantStatisticsService;

import java.util.Date;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RestaurantStatisticsServiceImpl implements RestaurantStatisticsService {
    OrderRepository orderRepository;
    DishRepository dishRepository;

    @Override
    public Double getTotalRevenue() {
        Stream<OrderEntity> paidOrders = orderRepository.streamAllBy()
                .filter(order -> order.getStatus() == OrderStatus.PAYED);

        return paidOrders
                .flatMap(order -> order.getDishes().entrySet().stream())
                .mapToDouble(entry -> {
                    String dishName = entry.getKey();
                    Long amount = entry.getValue();
                    DishEntity dish = dishRepository.findByName(dishName)
                            .orElseThrow(() -> new NotFoundException("Dish with name " + dishName + " not found"));
                    return dish.getPrice() * amount;
                })
                .sum();
    }

    @Override
    public Long getAmountOfPayedOrders() {
        return orderRepository.streamAllBy()
                .filter(order -> order.getStatus() == OrderStatus.PAYED)
                .count();
    }

    @Override
    public Long getOrderCountBetweenDates(Date startDate, Date endDate) {
        return orderRepository.streamAllBy()
                .filter(order -> order.getStartDate().after(startDate) && order.getStartDate().before(endDate))
                .count();
    }
}
