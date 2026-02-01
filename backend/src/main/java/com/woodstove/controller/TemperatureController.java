package com.woodstove.controller;

import com.woodstove.dto.TemperatureRequest;
import com.woodstove.dto.TemperatureStats;
import com.woodstove.model.TemperatureReading;
import com.woodstove.service.TemperatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TemperatureController {

    private final TemperatureService temperatureService;

    @PostMapping("/temperatures")
    public ResponseEntity<TemperatureReading> recordTemperature(@Valid @RequestBody TemperatureRequest request) {
        TemperatureReading reading = temperatureService.recordTemperature(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reading);
    }

    @GetMapping("/temperatures")
    public ResponseEntity<List<TemperatureReading>> getReadings(
            @RequestParam(required = false) String sensorId,
            @RequestParam(required = false, defaultValue = "100") Integer limit,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        List<TemperatureReading> readings = temperatureService.getReadings(sensorId, limit, from, to);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/temperatures/stats")
    public ResponseEntity<TemperatureStats> getStats(
            @RequestParam(required = false) String sensorId,
            @RequestParam(required = false, defaultValue = "day") String period) {
        TemperatureStats stats = temperatureService.getStats(sensorId, period);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/sensors")
    public ResponseEntity<List<Map<String, String>>> getSensors() {
        List<String> sensorIds = temperatureService.getAllSensorIds();
        List<Map<String, String>> sensors = sensorIds.stream()
                .map(id -> Map.of("sensorId", id))
                .toList();
        return ResponseEntity.ok(sensors);
    }
}
