package com.logistics.loadoptimizer.service;

import com.logistics.loadoptimizer.exception.InvalidInputException;
import com.logistics.loadoptimizer.exception.PayloadTooLargeException;
import com.logistics.loadoptimizer.model.Order;
import com.logistics.loadoptimizer.model.Truck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ValidationService {

    private static final int MAX_ORDERS = 22;

    public void validateTruck(Truck truck) {
        List<String> errors = new ArrayList<>();

        if (truck == null) {
            throw new InvalidInputException("Truck information is required");
        }

        if (truck.getId() == null || truck.getId().trim().isEmpty()) {
            errors.add("Truck ID is required");
        }

        if (truck.getMaxWeightLbs() == null || truck.getMaxWeightLbs() <= 0) {
            errors.add("Max weight must be greater than 0");
        }

        if (truck.getMaxVolumeCuft() == null || truck.getMaxVolumeCuft() <= 0) {
            errors.add("Max volume must be greater than 0");
        }

        if (!errors.isEmpty()) {
            throw new InvalidInputException("Truck validation failed", errors);
        }
    }

    public void validateOrders(List<Order> orders) {
        if (orders == null) {
            throw new InvalidInputException("Orders list is required");
        }

        if (orders.size() > MAX_ORDERS) {
            throw new PayloadTooLargeException("Maximum 22 orders allowed");
        }

        List<String> errors = new ArrayList<>();
        Set<String> orderIds = new HashSet<>();

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            String prefix = "Order[" + i + "]: ";

            if (!orderIds.add(order.getId())) {
                errors.add(prefix + "Duplicate order ID: " + order.getId());
            }

            if (!order.hasValidDates()) {
                errors.add(prefix + "Pickup date must be <= delivery date");
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidInputException("Order validation failed", errors);
        }
    }
}
