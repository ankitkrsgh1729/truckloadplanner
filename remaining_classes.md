// ============================================
// COMPLETE IMPLEMENTATIONS OF REMAINING CLASSES
// ============================================

// ============================================
// 1. MODEL CLASSES
// ============================================

package com.logistics.loadoptimizer.model;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private Long payoutCents;
    private Integer weightLbs;
    private Integer volumeCuft;
    private String origin;
    private String destination;
    private LocalDate pickupDate;
    private LocalDate deliveryDate;
    private Boolean isHazmat;
    
    public String getRouteKey() {
        return origin + "->" + destination;
    }
    
    public boolean hasValidDates() {
        return pickupDate != null && deliveryDate != null 
            && !pickupDate.isAfter(deliveryDate);
    }
    
    public boolean fitsInCapacity(int maxWeight, int maxVolume) {
        return weightLbs <= maxWeight && volumeCuft <= maxVolume;
    }
}

// --------------------------------------------

package com.logistics.loadoptimizer.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Truck {
    private String id;
    private Integer maxWeightLbs;
    private Integer maxVolumeCuft;
    
    public boolean isValid() {
        return id != null && !id.trim().isEmpty()
            && maxWeightLbs != null && maxWeightLbs > 0
            && maxVolumeCuft != null && maxVolumeCuft > 0;
    }
}

// --------------------------------------------

package com.logistics.loadoptimizer.model;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResult {
    private List<Order> selectedOrders;
    private Long totalPayoutCents;
    private Integer totalWeightLbs;
    private Integer totalVolumeCuft;
    
    public boolean isEmpty() {
        return selectedOrders == null || selectedOrders.isEmpty();
    }
}

// ============================================
// 2. DTO CLASSES
// ============================================

package com.logistics.loadoptimizer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationRequest {
    
    @NotNull(message = "Truck information is required")
    @Valid
    private TruckDto truck;
    
    @NotNull(message = "Orders list is required")
    @Size(max = 22, message = "Maximum 22 orders allowed")
    @Valid
    private List<OrderDto> orders;
}

// --------------------------------------------

package com.logistics.loadoptimizer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TruckDto {
    
    @NotBlank(message = "Truck ID is required")
    private String id;
    
    @NotNull(message = "Max weight is required")
    @Min(value = 1, message = "Max weight must be greater than 0")
    @JsonProperty("max_weight_lbs")
    private Integer maxWeightLbs;
    
    @NotNull(message = "Max volume is required")
    @Min(value = 1, message = "Max volume must be greater than 0")
    @JsonProperty("max_volume_cuft")
    private Integer maxVolumeCuft;
}

// --------------------------------------------

package com.logistics.loadoptimizer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    @NotBlank(message = "Order ID is required")
    private String id;
    
    @NotNull(message = "Payout is required")
    @Min(value = 0, message = "Payout must be non-negative")
    @JsonProperty("payout_cents")
    private Long payoutCents;
    
    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be greater than 0")
    @JsonProperty("weight_lbs")
    private Integer weightLbs;
    
    @NotNull(message = "Volume is required")
    @Min(value = 1, message = "Volume must be greater than 0")
    @JsonProperty("volume_cuft")
    private Integer volumeCuft;
    
    @NotBlank(message = "Origin is required")
    private String origin;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    @NotNull(message = "Pickup date is required")
    @JsonProperty("pickup_date")
    private LocalDate pickupDate;
    
    @NotNull(message = "Delivery date is required")
    @JsonProperty("delivery_date")
    private LocalDate deliveryDate;
    
    @NotNull(message = "Hazmat flag is required")
    @JsonProperty("is_hazmat")
    private Boolean isHazmat;
}

// --------------------------------------------

package com.logistics.loadoptimizer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResponse {
    
    @JsonProperty("truck_id")
    private String truckId;
    
    @JsonProperty("selected_order_ids")
    private List<String> selectedOrderIds;
    
    @JsonProperty("total_payout_cents")
    private Long totalPayoutCents;
    
    @JsonProperty("total_weight_lbs")
    private Integer totalWeightLbs;
    
    @JsonProperty("total_volume_cuft")
    private Integer totalVolumeCuft;
    
    @JsonProperty("utilization_weight_percent")
    private Double utilizationWeightPercent;
    
    @JsonProperty("utilization_volume_percent")
    private Double utilizationVolumePercent;
    
    public static OptimizationResponse from(
            String truckId,
            int maxWeightLbs,
            int maxVolumeCuft,
            com.logistics.loadoptimizer.model.OptimizationResult result) {
        
        List<String> orderIds = result.getSelectedOrders().stream()
            .map(com.logistics.loadoptimizer.model.Order::getId)
            .collect(Collectors.toList());
        
        double weightUtil = calculateUtilization(
            result.getTotalWeightLbs(), maxWeightLbs);
        double volumeUtil = calculateUtilization(
            result.getTotalVolumeCuft(), maxVolumeCuft);
        
        return OptimizationResponse.builder()
            .truckId(truckId)
            .selectedOrderIds(orderIds)
            .totalPayoutCents(result.getTotalPayoutCents())
            .totalWeightLbs(result.getTotalWeightLbs())
            .totalVolumeCuft(result.getTotalVolumeCuft())
            .utilizationWeightPercent(weightUtil)
            .utilizationVolumePercent(volumeUtil)
            .build();
    }
    
    private static double calculateUtilization(int used, int capacity) {
        if (capacity == 0) return 0.0;
        return BigDecimal.valueOf(used)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(capacity), 2, RoundingMode.HALF_UP)
            .doubleValue();
    }
}

// --------------------------------------------

package com.logistics.loadoptimizer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;
    
    public static ErrorResponse of(String error, String message) {
        return ErrorResponse.builder()
            .error(error)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    public static ErrorResponse of(String error, String message, List<String> details) {
        return ErrorResponse.builder()
            .error(error)
            .message(message)
            .details(details)
            .timestamp(LocalDateTime.now())
            .build();
    }
}

// ============================================
// 3. SERVICE CLASSES
// ============================================

package com.logistics.loadoptimizer.service;

import com.logistics.loadoptimizer.algorithm.OptimizationAlgorithm;
import com.logistics.loadoptimizer.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadOptimizerService {
    
    private final OptimizationAlgorithm optimizationAlgorithm;
    private final ValidationService validationService;
    private final RouteCompatibilityService routeCompatibilityService;
    
    public OptimizationResult optimize(Truck truck, List<Order> orders) {
        log.info("Starting optimization for truck: {}, orders: {}", 
            truck.getId(), orders.size());
        
        validationService.validateTruck(truck);
        validationService.validateOrders(orders);
        
        if (orders.isEmpty()) {
            return createEmptyResult();
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
        
        return bestHazmat.getTotalPayoutCents() > bestNonHazmat.getTotalPayoutCents()
            ? bestHazmat : bestNonHazmat;
    }
    
    private OptimizationResult findBestSingleHazmat(Truck truck, List<Order> hazmatOrders) {
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
            return createEmptyResult();
        }
        
        return OptimizationResult.builder()
            .selectedOrders(List.of(bestHazmat))
            .totalPayoutCents(bestHazmat.getPayoutCents())
            .totalWeightLbs(bestHazmat.getWeightLbs())
            .totalVolumeCuft(bestHazmat.getVolumeCuft())
            .build();
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

// --------------------------------------------

package com.logistics.loadoptimizer.service;

import com.logistics.loadoptimizer.exception.InvalidInputException;
import com.logistics.loadoptimizer.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class ValidationService {
    
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

// --------------------------------------------

package com.logistics.loadoptimizer.service;

import com.logistics.loadoptimizer.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RouteCompatibilityService {
    
    public Map<String, List<Order>> groupByRoute(List<Order> orders) {
        return orders.stream()
            .collect(Collectors.groupingBy(Order::getRouteKey));
    }
}

// ============================================
// 4. ALGORITHM INTERFACE & IMPLEMENTATION
// ============================================

package com.logistics.loadoptimizer.algorithm;

import com.logistics.loadoptimizer.model.*;
import java.util.List;

public interface OptimizationAlgorithm {
    OptimizationResult optimize(
        List<Order> orders,
        int maxWeightLbs,
        int maxVolumeCuft
    );
}