package com.woodstove.repository;

import com.woodstove.model.TemperatureReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TemperatureRepository extends JpaRepository<TemperatureReading, Long> {

    List<TemperatureReading> findBySensorIdOrderByRecordedAtDesc(String sensorId);

    List<TemperatureReading> findByRecordedAtBetweenOrderByRecordedAtDesc(Instant from, Instant to);

    List<TemperatureReading> findBySensorIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            String sensorId, Instant from, Instant to);

    @Query("SELECT DISTINCT t.sensorId FROM TemperatureReading t ORDER BY t.sensorId")
    List<String> findAllSensorIds();

    Optional<TemperatureReading> findFirstBySensorIdOrderByRecordedAtDesc(String sensorId);

    Optional<TemperatureReading> findFirstByOrderByRecordedAtDesc();

    @Query("SELECT MIN(t.temperature) FROM TemperatureReading t WHERE t.recordedAt >= :from")
    Optional<BigDecimal> findMinTemperatureSince(@Param("from") Instant from);

    @Query("SELECT MAX(t.temperature) FROM TemperatureReading t WHERE t.recordedAt >= :from")
    Optional<BigDecimal> findMaxTemperatureSince(@Param("from") Instant from);

    @Query("SELECT AVG(t.temperature) FROM TemperatureReading t WHERE t.recordedAt >= :from")
    Optional<BigDecimal> findAvgTemperatureSince(@Param("from") Instant from);

    @Query("SELECT COUNT(t) FROM TemperatureReading t WHERE t.recordedAt >= :from")
    Long countReadingsSince(@Param("from") Instant from);

    @Query("SELECT MIN(t.temperature) FROM TemperatureReading t WHERE t.sensorId = :sensorId AND t.recordedAt >= :from")
    Optional<BigDecimal> findMinTemperatureBySensorIdSince(@Param("sensorId") String sensorId, @Param("from") Instant from);

    @Query("SELECT MAX(t.temperature) FROM TemperatureReading t WHERE t.sensorId = :sensorId AND t.recordedAt >= :from")
    Optional<BigDecimal> findMaxTemperatureBySensorIdSince(@Param("sensorId") String sensorId, @Param("from") Instant from);

    @Query("SELECT AVG(t.temperature) FROM TemperatureReading t WHERE t.sensorId = :sensorId AND t.recordedAt >= :from")
    Optional<BigDecimal> findAvgTemperatureBySensorIdSince(@Param("sensorId") String sensorId, @Param("from") Instant from);

    @Query("SELECT COUNT(t) FROM TemperatureReading t WHERE t.sensorId = :sensorId AND t.recordedAt >= :from")
    Long countReadingsBySensorIdSince(@Param("sensorId") String sensorId, @Param("from") Instant from);

    @Query(value = "SELECT * FROM temperature_readings ORDER BY recorded_at DESC LIMIT :limit", nativeQuery = true)
    List<TemperatureReading> findRecentReadings(@Param("limit") int limit);

    @Query(value = "SELECT * FROM temperature_readings WHERE sensor_id = :sensorId ORDER BY recorded_at DESC LIMIT :limit", nativeQuery = true)
    List<TemperatureReading> findRecentReadingsBySensorId(@Param("sensorId") String sensorId, @Param("limit") int limit);
}
