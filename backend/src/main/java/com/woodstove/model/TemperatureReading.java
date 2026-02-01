package com.woodstove.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "temperature_readings", indexes = {
    @Index(name = "idx_sensor_recorded", columnList = "sensor_id, recorded_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false, length = 50)
    private String sensorId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (recordedAt == null) {
            recordedAt = createdAt;
        }
    }
}
