package ru.previak.restaurant.services.interfaces;

import java.util.Date;

public interface RestaurantStatisticsService {
    Double getTotalRevenue();
    Long getAmountOfPayedOrders();
    Long getOrderCountBetweenDates(Date startDate, Date endDate);
}
