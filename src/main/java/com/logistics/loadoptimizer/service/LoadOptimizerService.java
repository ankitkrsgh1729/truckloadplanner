package com.logistics.loadoptimizer.service;

import com.logistics.loadoptimizer.algorithm.OptimizationAlgorithm;
import com.logistics.loadoptimizer.model.OptimizationResult;
import com.logistics.loadoptimizer.model.Order;
import com.logistics.loadoptimizer.model.Truck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadOptimizerService {

    private final OptimizationAlgorithm optimizationAlgorithm;
    private final ValidationService validationService;
    private final RouteCompatibilityService routeCompatibilityService;

    public OptimizationResult optimize(Truck truck, List<Order> orders) {
              validationService.validateTruck(truck);
        validationService.validateOrders(orders);

        if (orders.isEmpty()) {
            log.warn("No orders provided for optimization");
            OptimizationResult empty = createEmptyResult();
            log.debug("EXIT service.optimize empty result");
            return empty;
        }

        Map<String, List<Order>> routeGroups =
            routeCompatibilityService.groupByRoute(orders);

        OptimizationResult bestResult = createEmptyResult();

        for (Map.Entry<String, List<Order>> entry : routeGroups.entrySet()) {
            OptimizationResult result = optimizeRouteGroup(
                truck, entry.getValue());

            if (result.getTotalPayoutCents() > bestResult.getTotalPayoutCents()) {
                bestResult = result;
            }
        }
        return bestResult;
    }

    private OptimizationResult optimizeRouteGroup(Truck truck, List<Order> orders) {
        log.debug("ENTER optimizeRouteGroup routeKey={} size={}",
            orders.isEmpty() ? "none" : orders.get(0).getRouteKey(), orders.size());
        List<Order> hazmatOrders = new ArrayList<>();
        List<Order> nonHazmatOrders = new ArrayList<>();

        for (Order order : orders) {
            if (Boolean.TRUE.equals(order.getIsHazmat())) {
                hazmatOrders.add(order);
            } else {
                nonHazmatOrders.add(order);
            }
        }

        OptimizationResult bestHazmat = findBestSingleHazmat(truck, hazmatOrders);
        OptimizationResult bestNonHazmat = optimizationAlgorithm.optimize(
            nonHazmatOrders, truck.getMaxWeightLbs(), truck.getMaxVolumeCuft());

        OptimizationResult chosen = bestHazmat.getTotalPayoutCents() > bestNonHazmat.getTotalPayoutCents()
            ? bestHazmat : bestNonHazmat;

        log.debug("EXIT optimizeRouteGroup routeKey={} chosenPayout={}",
            orders.isEmpty() ? "none" : orders.get(0).getRouteKey(),
            chosen.getTotalPayoutCents());
        return chosen;
    }

    private OptimizationResult findBestSingleHazmat(Truck truck, List<Order> hazmatOrders) {
        log.debug("ENTER findBestSingleHazmat count={}", hazmatOrders.size());
        Order bestHazmat = null;
        long maxPayout = 0;

        for (Order order : hazmatOrders) {
            if (order.fitsInCapacity(truck.getMaxWeightLbs(), truck.getMaxVolumeCuft())
                && order.getPayoutCents() > maxPayout) {
                bestHazmat = order;
                maxPayout = order.getPayoutCents();
            }
        }

        if (bestHazmat == null) {
            OptimizationResult empty = createEmptyResult();
            log.debug("EXIT findBestSingleHazmat none-fit");
            return empty;
        }

        OptimizationResult result = OptimizationResult.builder()
            .selectedOrders(List.of(bestHazmat))
            .totalPayoutCents(bestHazmat.getPayoutCents())
            .totalWeightLbs(bestHazmat.getWeightLbs())
            .totalVolumeCuft(bestHazmat.getVolumeCuft())
            .build();
        log.debug("EXIT findBestSingleHazmat payout={}", result.getTotalPayoutCents());
        return result;
    }

    private OptimizationResult createEmptyResult() {
        return OptimizationResult.builder()
            .selectedOrders(Collections.emptyList())
            .totalPayoutCents(0L)
            .totalWeightLbs(0)
            .totalVolumeCuft(0)
            .build();
    }
}
