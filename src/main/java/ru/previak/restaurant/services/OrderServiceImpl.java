package ru.previak.restaurant.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
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

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class OrderServiceImpl implements OrderService {
    final OrderRepository orderRepository;
    final DishRepository dishRepository;
    final MenuItemRepository menuItemRepository;
    final OrderMapper orderMapper;

    final ExecutorService executorService = Executors.newCachedThreadPool();
    final List<CompletableFuture<Void>> cookingFutures = new ArrayList<>();
    final ConcurrentHashMap<String, Long> newDishes = new ConcurrentHashMap<>();
    boolean isCancelled = false;

    @Transactional
    @Override
    public Long createOrder(Long userId) {
        if (orderRepository.existsByUserIdAndStatusIn(userId, Arrays.asList(OrderStatus.PENDING, OrderStatus.COOKING))) {
            throw new BadRequestException("User already has an active order");
        }
        if (orderRepository.existsByUserIdAndStatus(userId, OrderStatus.DONE)) {
            throw new BadRequestException("User already has an order with status DONE, pay for it");
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.setStatus(OrderStatus.PENDING);
        orderRepository.save(orderEntity);
        return orderEntity.getId();
    }
    public synchronized Long getAmountOfDishes(ConcurrentHashMap<String, Long> dishes) {
        Long amount = 0L;
        for (Map.Entry<String, Long> entry : dishes.entrySet()) {
            amount += entry.getValue();
        }
        return amount;
    }

    private CompletableFuture<Void> cookDish(Long orderId, DishEntity dishEntity) {
        return CompletableFuture.runAsync(() -> {
            System.out.println("Order " + orderId + ": Dish " + dishEntity.getName() + " has begun to cook");
            try {
                TimeUnit.MINUTES.sleep(dishEntity.getCookingTimeInMinutes());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (isCancelled) {
                return;
            }

            System.out.println("Order " + orderId + ": Dish " + dishEntity.getName() + " is ready");
        }, executorService);
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

        Map<String, Long> dishes = orderEntity.getDishes();
        if (dishes.containsKey(dishName)) {
            dishes.put(dishEntity.getName(), amount + dishes.get(dishName));
            if (orderEntity.getStatus() == OrderStatus.COOKING) {
                newDishes.put(dishEntity.getName(), amount + dishes.get(dishName));
            }
        } else {
            dishes.put(dishEntity.getName(), amount);
            if (orderEntity.getStatus() == OrderStatus.COOKING) {
                newDishes.put(dishEntity.getName(), amount);
            }
        }

        if (menuItemEntity.getAmount() == 0) {
            throw new BadRequestException("There is 0 " + dishName + " in menu");
        }
        if (menuItemEntity.getAmount() - amount < 0) {
            menuItemEntity.setAmount(0L);
        } else {
            menuItemEntity.setAmount(menuItemEntity.getAmount() - amount);
        }

        orderEntity.setDishes(dishes);
        orderRepository.save(orderEntity);

        if (orderEntity.getStatus() == OrderStatus.COOKING) {
            for (int i = 0; i < amount; ++i) {
                cookingFutures.add(cookDish(orderId, dishEntity));
            }
            CompletableFuture.allOf(cookingFutures.toArray(new CompletableFuture[0])).join();
            newDishes.remove(dishName);

            if (isCancelled) {
                newDishes.clear();
                return;
            }
            if (newDishes.isEmpty()) {
                orderEntity.setStatus(OrderStatus.DONE);
                System.out.println("Dishes from order " + orderId + " are finished");
                orderEntity.setEndDate(new Date());
            }
        }
        orderRepository.save(orderEntity);
    }

    @Transactional
    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        Stream<OrderEntity> userOrders = orderRepository.streamAllByUserId(userId);

        return userOrders
                .map(orderMapper::orderEntity2OrderDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void submitCookingOrder(Long orderId, Long userId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        if (!orderEntity.getUserId().equals(userId)) {
            throw new BadRequestException("You can not start someone's else order");
        }

        if (orderEntity.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is already " + orderEntity.getStatus().name());
        }

        Map<String, Long> dishes = orderEntity.getDishes();
        if (dishes == null || dishes.isEmpty()) {
            throw new BadRequestException("There are no dishes in this order");
        }

        orderEntity.setStatus(OrderStatus.COOKING);
        System.out.println("Order " + orderId + " cooking process has begun");
        orderEntity.setStartDate(new Date());
        orderRepository.save(orderEntity);


        for (Map.Entry<String, Long> entry : dishes.entrySet()) {
            DishEntity dish = dishRepository.findByName(entry.getKey())
                    .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));
            Long amount = entry.getValue();

            for (int i = 0; i < amount; ++i) {
                cookingFutures.add(cookDish(orderId, dish));
            }
        }

        CompletableFuture.allOf(cookingFutures.toArray(new CompletableFuture[0])).join();

        if (isCancelled) {
            return;
        }

        if (newDishes.isEmpty()) {
            orderEntity.setStatus(OrderStatus.DONE);
            System.out.println("Dishes from order " + orderId + " are finished");
            orderEntity.setEndDate(new Date());
        }

        orderRepository.save(orderEntity);
    }
    @Transactional
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

        isCancelled = true;
        System.out.println("Order " + orderId + " cooking process cancelled");
        order.setStatus(OrderStatus.CANCELED);
        order.setEndDate(new Date());
        orderRepository.save(order);
    }

    @Transactional
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
