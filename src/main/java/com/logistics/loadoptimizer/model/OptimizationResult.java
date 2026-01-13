package com.logistics.loadoptimizer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
