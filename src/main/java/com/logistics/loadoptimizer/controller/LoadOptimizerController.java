package com.logistics.loadoptimizer.controller;

import com.logistics.loadoptimizer.dto.request.OptimizationRequest;
import com.logistics.loadoptimizer.dto.response.OptimizationResponse;
import com.logistics.loadoptimizer.model.Order;
import com.logistics.loadoptimizer.model.OptimizationResult;
import com.logistics.loadoptimizer.model.Truck;
import com.logistics.loadoptimizer.service.LoadOptimizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/load-optimizer")
@RequiredArgsConstructor
public class LoadOptimizerController {

    private final LoadOptimizerService optimizerService;

    @PostMapping("/optimize")
    public ResponseEntity<OptimizationResponse> optimize(
        @Valid @RequestBody OptimizationRequest request
    ) {
        log.info("ENTER optimize: truckId={}, orders={}",
            request.getTruck().getId(), request.getOrders().size());

        Truck truck = convertToTruck(request.getTruck());
        List<Order> orders = request.getOrders().stream()
            .map(this::convertToOrder)
            .collect(Collectors.toList());

        OptimizationResult result = optimizerService.optimize(truck, orders);

        OptimizationResponse response = OptimizationResponse.from(
            truck.getId(),
            truck.getMaxWeightLbs(),
            truck.getMaxVolumeCuft(),
            result
        );

        log.info("EXIT optimize: selectedOrders={}, totalPayoutCents={}",
            response.getSelectedOrderIds().size(), response.getTotalPayoutCents());

        return ResponseEntity.ok(response);
    }

    private Truck convertToTruck(com.logistics.loadoptimizer.dto.request.TruckDto dto) {
        return Truck.builder()
            .id(dto.getId())
            .maxWeightLbs(dto.getMaxWeightLbs())
            .maxVolumeCuft(dto.getMaxVolumeCuft())
            .build();
    }

    private Order convertToOrder(com.logistics.loadoptimizer.dto.request.OrderDto dto) {
        return Order.builder()
            .id(dto.getId())
            .payoutCents(dto.getPayoutCents())
            .weightLbs(dto.getWeightLbs())
            .volumeCuft(dto.getVolumeCuft())
            .origin(dto.getOrigin())
            .destination(dto.getDestination())
            .pickupDate(dto.getPickupDate())
            .deliveryDate(dto.getDeliveryDate())
            .isHazmat(dto.getIsHazmat())
            .build();
    }
}
