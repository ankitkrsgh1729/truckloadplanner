package com.logistics.loadoptimizer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
