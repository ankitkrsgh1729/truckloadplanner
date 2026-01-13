package com.logistics.loadoptimizer.algorithm;

import com.logistics.loadoptimizer.model.OptimizationResult;
import com.logistics.loadoptimizer.model.Order;

import java.util.List;

public interface OptimizationAlgorithm {
    OptimizationResult optimize(
        List<Order> orders,
        int maxWeightLbs,
        int maxVolumeCuft
    );
}
