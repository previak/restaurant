package ru.previak.restaurant.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.previak.restaurant.dto.OrderDTO;
import ru.previak.restaurant.entities.DishEntity;
import ru.previak.restaurant.entities.MenuItemEntity;
import ru.previak.restaurant.entities.OrderEntity;
import ru.previak.restaurant.entities.OrderStatus;
import ru.previak.restaurant.exceptions.BadRequestException;
import ru.previak.restaurant.exceptions.NotFoundException;
import ru.previak.restaurant.mapper.OrderMapper;
import ru.previak.restaurant.repositories.DishRepository;
import ru.previak.restaurant.repositories.MenuItemRepository;
import ru.previak.restaurant.repositories.OrderRepository;
import ru.previak.restaurant.services.interfaces.OrderService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    DishRepository dishRepository;
    MenuItemRepository menuItemRepository;
    OrderMapper orderMapper;

    ExecutorService executorService = Executors.newCachedThreadPool();
    List<CompletableFuture<Void>> cookingTasks = new ArrayList<>();

    @Override
    public Long createOrder(Long userId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.setStatus(OrderStatus.PENDING);
        orderRepository.save(orderEntity);
        return orderEntity.getId();
    }

    @Override
    public void addDishToOrder(Long orderId, String dishName, Long amount, Long userId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        if (!orderEntity.getUserId().equals(userId)) {
            throw new BadRequestException("You can not add dish to someone's else order");
        }

        OrderStatus orderStatus = orderEntity.getStatus();

        if (orderStatus == OrderStatus.DONE || orderStatus == OrderStatus.PAYED || orderStatus == OrderStatus.CANCELED) {
            throw new BadRequestException("This order is already " + orderStatus.name() + ". Create a new one");
        }

        DishEntity dishEntity = dishRepository.findByName(dishName)
                .orElseThrow(() -> new NotFoundException("Dish with name " + dishName + " not found"));

        MenuItemEntity menuItemEntity = menuItemRepository.findByDishName(dishEntity.getName())
                .orElseThrow(() -> new NotFoundException("Menu item with dish " + dishName + " not found"));

        if (orderEntity.getStatus() == OrderStatus.DONE) {
            return;
        }

        Map<String, Long> dishMap = orderEntity.getDishes();
        dishMap.put(dishEntity.getName(), amount);
        if (menuItemEntity.getAmount() == 0) {
            throw new BadRequestException("There is 0 " + dishName + " in menu");
        }
        if (menuItemEntity.getAmount() - amount < 0) {
            menuItemEntity.setAmount(0L);
        } else {
            menuItemEntity.setAmount(menuItemEntity.getAmount() - amount);
        }
        orderEntity.setDishes(dishMap);

        if (orderEntity.getStatus() == OrderStatus.COOKING) {
            for (int i = 0; i < amount; ++i) {
                DishEntity dish = dishRepository.findByName(dishName)
                        .orElseThrow(() -> new NotFoundException("\\u001B[32mDish with name " + dishName + " not found"));
                Long cookingTimeInMinutes = dish.getCookingTimeInMinutes();

                final int finalI = i;
                CompletableFuture<Void> dishTask = CompletableFuture.runAsync(() -> {
                    try {
                        System.out.println("\\u001B[32mDish " + dishName + " #" + (finalI + 1) + " has begun to cook\\u001B[0m");
                        TimeUnit.MINUTES.sleep(cookingTimeInMinutes);
                        System.out.println("\\u001B[32mDish " + dishName + " #" + (finalI + 1) + " is ready\\u001B[0m");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("\\u001B[32mTask was interrupted: " + e.getMessage() + "\\u001B[0m");
                    }
                }, executorService);

                cookingTasks.add(dishTask);
            }
        }

        orderRepository.save(orderEntity);
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        Stream<OrderEntity> userOrders = orderRepository.streamAllByUserId(userId);
        return userOrders
                .map(orderMapper::orderEntity2OrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void submitCookingOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("You can not start someone's else order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is already " + order.getStatus().name());
        }

        Map<String, Long> dishes = order.getDishes();
        if (dishes == null || dishes.isEmpty()) {
            throw new BadRequestException("There are no dishes in this order");
        }

        order.setStatus(OrderStatus.COOKING);
        System.out.println("\\u001B[32mOrder " + orderId + " cooking process has begun\\u001B[0m");
        order.setStartDate(new Date());
        orderRepository.save(order);

        for (Map.Entry<String, Long> entry : dishes.entrySet()) {
            String dishName = entry.getKey();
            DishEntity dish = dishRepository.findByName(dishName)
                    .orElseThrow(() -> new NotFoundException("\\u001B[32mOrder with id " + orderId + " not found\\u001B[0m"));
            Long cookingTimeInMinutes = dish.getCookingTimeInMinutes();

            List<CompletableFuture<Void>> dishTasks = IntStream.range(0, entry.getValue().intValue())
                    .mapToObj(i -> CompletableFuture.runAsync(() -> {
                        try {
                            System.out.println("\\u001B[32mDish " + dishName + " #" + (i + 1) + " has begun to cook\\u001B[0m");
                            TimeUnit.MINUTES.sleep(cookingTimeInMinutes);
                            System.out.println("\\u001B[32mDish " + dishName + " #" + (i + 1) + " is ready\\u001B[0m");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("\\u001B[32mTask was interrupted: " + e.getMessage() + "\\u001B[0m");
                        }
                    }, executorService))
                    .toList();

            cookingTasks.addAll(dishTasks);
        }

        CompletableFuture.allOf(cookingTasks.toArray(new CompletableFuture[0])).join();

        order.setStatus(OrderStatus.DONE);
        System.out.println("\\u001B[32mOrder " + orderId + " is finished\\u001B[0m");
        order.setEndDate(new Date());

        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("You can not cancel someone's else order");
        }

        OrderStatus orderStatus = order.getStatus();

        if (orderStatus == OrderStatus.DONE || orderStatus == OrderStatus.PAYED || orderStatus == OrderStatus.CANCELED) {
            throw new BadRequestException("This order is already " + orderStatus.name());
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        for (CompletableFuture<Void> task : cookingTasks) {
            if (!task.isDone()) {
                task.cancel(true);
            }
        }

        cookingTasks.removeIf(CompletableFuture::isDone);
    }

    @Override
    public void payOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("You can not pay for someone's else order");
        }

        OrderStatus orderStatus = order.getStatus();

        if (orderStatus == OrderStatus.DONE) {
            order.setStatus(OrderStatus.PAYED);
            orderRepository.save(order);
        } else if (orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.COOKING) {
            throw new BadRequestException("Order is not done yet");
        } else if (orderStatus == OrderStatus.PAYED || orderStatus == OrderStatus.CANCELED) {
            throw new BadRequestException("Order is already " + orderStatus.name());
        }
    }

    public void shutdownExecutorService() {
        executorService.shutdown();
    }
}
