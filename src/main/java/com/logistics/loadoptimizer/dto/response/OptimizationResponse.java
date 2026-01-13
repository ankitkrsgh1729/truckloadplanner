package com.logistics.loadoptimizer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        com.logistics.loadoptimizer.model.OptimizationResult result
    ) {
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
