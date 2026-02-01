# Woodstove Temperature Monitor

A full-stack application to record and display temperature sensor data from a Raspberry Pi.

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Raspberry Pi   │────▶│  Spring Boot    │────▶│   PostgreSQL    │
│  (Sensor)       │ API │  Backend        │     │   Database      │
└─────────────────┘     └────────┬────────┘     └─────────────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │    SvelteKit    │
                        │    Frontend     │
                        └─────────────────┘
```

## Prerequisites

- Java 17+
- Node.js 18+
- Docker and Docker Compose

## Quick Start

### 1. Start the Database

```bash
docker-compose up -d
```

### 2. Start the Backend

```bash
cd backend
./gradlew bootRun
```

The API will be available at http://localhost:8080

### 3. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The dashboard will be available at http://localhost:5173

## API Endpoints

### POST /api/temperatures
Record a temperature reading from a sensor.

```bash
curl -X POST http://localhost:8080/api/temperatures \
  -H "Content-Type: application/json" \
  -d '{"sensorId":"pi-sensor-01","temperature":72.5}'
```

### GET /api/temperatures
Get recent readings with optional filters.

Query parameters:
- `sensorId` - Filter by sensor ID
- `limit` - Number of readings (default: 100)
- `from` - Start timestamp (ISO 8601)
- `to` - End timestamp (ISO 8601)

```bash
curl http://localhost:8080/api/temperatures
curl "http://localhost:8080/api/temperatures?sensorId=pi-sensor-01&limit=50"
```

### GET /api/temperatures/stats
Get temperature statistics.

Query parameters:
- `sensorId` - Filter by sensor ID
- `period` - Time period: `hour`, `day`, or `week` (default: day)

```bash
curl http://localhost:8080/api/temperatures/stats
curl "http://localhost:8080/api/temperatures/stats?period=week"
```

### GET /api/sensors
List all known sensors.

```bash
curl http://localhost:8080/api/sensors
```

## Raspberry Pi Integration

Example Python script to send temperature readings:

```python
import requests
import time
from datetime import datetime

API_URL = "http://your-server:8080/api/temperatures"
SENSOR_ID = "pi-sensor-01"

def read_temperature():
    # Read from your temperature sensor
    # This is an example - replace with actual sensor reading
    import random
    return round(random.uniform(65, 85), 1)

def send_reading(temperature):
    data = {
        "sensorId": SENSOR_ID,
        "temperature": temperature
    }
    response = requests.post(API_URL, json=data)
    response.raise_for_status()
    return response.json()

if __name__ == "__main__":
    while True:
        try:
            temp = read_temperature()
            result = send_reading(temp)
            print(f"Sent: {temp}° at {datetime.now()}")
        except Exception as e:
            print(f"Error: {e}")
        time.sleep(60)  # Send every minute
```

## Project Structure

```
woodstove/
├── backend/                    # Spring Boot application
│   ├── src/main/java/com/woodstove/
│   │   ├── WoodstoveApplication.java
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── dto/
│   └── build.gradle
├── frontend/                   # SvelteKit application
│   ├── src/
│   │   ├── routes/
│   │   └── lib/
│   └── package.json
├── docker-compose.yml
└── README.md
```
