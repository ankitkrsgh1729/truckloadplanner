package com.logistics.loadoptimizer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
