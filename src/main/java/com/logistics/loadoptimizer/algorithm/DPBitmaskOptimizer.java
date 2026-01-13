package com.logistics.loadoptimizer.algorithm;

import com.logistics.loadoptimizer.model.Order;
import com.logistics.loadoptimizer.model.OptimizationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DPBitmaskOptimizer implements OptimizationAlgorithm {

    private static class State {
        final long payout;
        final int weight;
        final int volume;
        final long mask;

        State(long payout, int weight, int volume, long mask) {
            this.payout = payout;
            this.weight = weight;
            this.volume = volume;
            this.mask = mask;
        }
    }
    @Override
    public OptimizationResult optimize(
        List<Order> orders,
        int maxWeightLbs,
        int maxVolumeCuft
    ) {
        if (orders == null || orders.isEmpty()) {
            return createEmptyResult();
        }

        int n = orders.size();
        if (n > 22) {
            log.warn("Order count {} exceeds recommended limit of 22", n);
        }

        Map<Long, State> dp = new HashMap<>();
        dp.put(0L, new State(0, 0, 0, 0L));

        long totalStates = 1L << n;

        for (long mask = 0; mask < totalStates; mask++) {
            State current = dp.get(mask);
            if (current == null) continue;

            for (int i = 0; i < n; i++) {
                long bit = 1L << i;
                if ((mask & bit) != 0) continue;

                Order order = orders.get(i);
                int newWeight = current.weight + order.getWeightLbs();
                int newVolume = current.volume + order.getVolumeCuft();

                if (newWeight > maxWeightLbs || newVolume > maxVolumeCuft) {
                    continue;
                }

                long newMask = mask | bit;
                long newPayout = current.payout + order.getPayoutCents();

                State existing = dp.get(newMask);
                if (existing == null || existing.payout < newPayout) {
                    dp.put(newMask, new State(newPayout, newWeight, newVolume, newMask));
                }
            }
        }

        State best = dp.values().stream()
            .max(Comparator.comparingLong(s -> s.payout))
            .orElse(new State(0, 0, 0, 0L));

        List<Order> selectedOrders = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((best.mask & (1L << i)) != 0) {
                selectedOrders.add(orders.get(i));
            }
        }

        return OptimizationResult.builder()
            .selectedOrders(selectedOrders)
            .totalPayoutCents(best.payout)
            .totalWeightLbs(best.weight)
            .totalVolumeCuft(best.volume)
            .build();
    }

    private OptimizationResult createEmptyResult() {
        return OptimizationResult.builder()
            .selectedOrders(List.of())
            .totalPayoutCents(0L)
            .totalWeightLbs(0)
            .totalVolumeCuft(0)
            .build();
    }
}
