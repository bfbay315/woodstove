package com.woodstove.service;

import com.woodstove.dto.TemperatureRequest;
import com.woodstove.dto.TemperatureStats;
import com.woodstove.model.TemperatureReading;
import com.woodstove.repository.TemperatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemperatureService {

    private final TemperatureRepository repository;

    @Transactional
    public TemperatureReading recordTemperature(TemperatureRequest request) {
        TemperatureReading reading = TemperatureReading.builder()
                .sensorId(request.getSensorId())
                .temperature(request.getTemperature())
                .recordedAt(request.getRecordedAt())
                .build();
        return repository.save(reading);
    }

    @Transactional(readOnly = true)
    public List<TemperatureReading> getReadings(String sensorId, Integer limit, Instant from, Instant to) {
        int effectiveLimit = limit != null ? limit : 100;

        if (sensorId != null && from != null && to != null) {
            return repository.findBySensorIdAndRecordedAtBetweenOrderByRecordedAtDesc(sensorId, from, to)
                    .stream()
                    .limit(effectiveLimit)
                    .toList();
        } else if (sensorId != null) {
            return repository.findRecentReadingsBySensorId(sensorId, effectiveLimit);
        } else if (from != null && to != null) {
            return repository.findByRecordedAtBetweenOrderByRecordedAtDesc(from, to)
                    .stream()
                    .limit(effectiveLimit)
                    .toList();
        } else {
            return repository.findRecentReadings(effectiveLimit);
        }
    }

    @Transactional(readOnly = true)
    public TemperatureStats getStats(String sensorId, String period) {
        Instant periodStart = calculatePeriodStart(period);
        Instant periodEnd = Instant.now();

        BigDecimal current;
        BigDecimal min;
        BigDecimal max;
        BigDecimal avg;
        Long count;

        if (sensorId != null) {
            current = repository.findFirstBySensorIdOrderByRecordedAtDesc(sensorId)
                    .map(TemperatureReading::getTemperature)
                    .orElse(null);
            min = repository.findMinTemperatureBySensorIdSince(sensorId, periodStart).orElse(null);
            max = repository.findMaxTemperatureBySensorIdSince(sensorId, periodStart).orElse(null);
            avg = repository.findAvgTemperatureBySensorIdSince(sensorId, periodStart)
                    .map(a -> a.setScale(2, RoundingMode.HALF_UP))
                    .orElse(null);
            count = repository.countReadingsBySensorIdSince(sensorId, periodStart);
        } else {
            current = repository.findFirstByOrderByRecordedAtDesc()
                    .map(TemperatureReading::getTemperature)
                    .orElse(null);
            min = repository.findMinTemperatureSince(periodStart).orElse(null);
            max = repository.findMaxTemperatureSince(periodStart).orElse(null);
            avg = repository.findAvgTemperatureSince(periodStart)
                    .map(a -> a.setScale(2, RoundingMode.HALF_UP))
                    .orElse(null);
            count = repository.countReadingsSince(periodStart);
        }

        return TemperatureStats.builder()
                .current(current)
                .min(min)
                .max(max)
                .avg(avg)
                .readingCount(count)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .sensorId(sensorId)
                .build();
    }

    @Transactional(readOnly = true)
    public List<String> getAllSensorIds() {
        return repository.findAllSensorIds();
    }

    private Instant calculatePeriodStart(String period) {
        Instant now = Instant.now();
        return switch (period != null ? period.toLowerCase() : "day") {
            case "hour" -> now.minus(1, ChronoUnit.HOURS);
            case "week" -> now.minus(7, ChronoUnit.DAYS);
            default -> now.minus(1, ChronoUnit.DAYS);
        };
    }
}
