package com.logistics.loadoptimizer.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationRequest {

    @NotNull(message = "Truck information is required")
    @Valid
    private TruckDto truck;

    @NotNull(message = "Orders list is required")
    @Valid
    private List<OrderDto> orders;
}
