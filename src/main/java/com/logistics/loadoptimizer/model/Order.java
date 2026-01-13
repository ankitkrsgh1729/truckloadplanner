package com.logistics.loadoptimizer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
