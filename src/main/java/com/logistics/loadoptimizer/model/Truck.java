package com.logistics.loadoptimizer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
