package com.logistics.loadoptimizer.service;

import com.logistics.loadoptimizer.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RouteCompatibilityService {

    public Map<String, List<Order>> groupByRoute(List<Order> orders) {
        return orders.stream()
            .collect(Collectors.groupingBy(Order::getRouteKey));
    }
}
