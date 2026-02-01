package com.woodstove.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureRequest {

    @NotBlank(message = "Sensor ID is required")
    private String sensorId;

    @NotNull(message = "Temperature is required")
    private BigDecimal temperature;

    private Instant recordedAt;
}
