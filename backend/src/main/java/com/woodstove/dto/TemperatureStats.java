package com.woodstove.dto;

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
public class TemperatureStats {

    private BigDecimal current;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal avg;
    private Long readingCount;
    private Instant periodStart;
    private Instant periodEnd;
    private String sensorId;
}
