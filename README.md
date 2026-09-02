# Ride Booking Backend
 
A scalable ride-booking backend built with Spring Boot that handles ride creation, driver management, nearby-driver discovery, driver assignment, fare calculation, ride lifecycle management, and real-time driver location updates.
 
The system uses PostgreSQL for persistent data storage and Apache Kafka for asynchronous event-driven communication between ride and driver components.
 
---
 
## Features
 
### Ride Management
- Create a new ride
- Calculate ride distance
- Calculate ride fare
- Assign a driver to a ride
- Accept a ride
- Start a ride
- Complete a ride
- Cancel a ride
- Track ride status
### Driver Management
- Register drivers
- Maintain driver availability
- Track driver status
- Update driver location
- Find nearby available drivers
- Assign drivers to rides
### Location Services
- Receive real-time driver location updates
- Calculate distance between coordinates using the Haversine formula
- Find available drivers within a specified radius
### Fare Calculation
- Base fare calculation
- Distance-based pricing
- Automatic fare calculation during ride creation
### Event-Driven Architecture
- Publish ride lifecycle events using Kafka
- Consume driver location updates
- Decouple ride processing from asynchronous operations
### API Documentation
- REST APIs
- Interactive Swagger/OpenAPI documentation
### Persistence
- PostgreSQL database
- JPA/Hibernate ORM
- UUID-based entity identifiers
---
 
## Architecture
 
<img width="491" height="371" alt="image" src="https://github.com/user-attachments/assets/5753a193-7472-43c3-90a2-6697c5609177" />

 
### Ride Creation Flow
 
1. Rider submits a ride request from the React frontend.
2. Spring Boot receives the request through the REST controller.
3. The service calculates the distance between pickup and destination.
4. Fare is calculated based on the distance.
5. The ride is persisted in PostgreSQL.
6. A `RIDE_CREATED` event is published to Kafka.
7. The system searches for nearby available drivers.
8. A suitable driver is assigned to the ride.
9. The ride status is updated accordingly.
### Driver Location Update Flow
 
1. Driver sends latitude and longitude.
2. Spring Boot receives the location update.
3. A `DRIVER_LOCATION_UPDATE` event is published to Kafka.
4. Kafka consumers process the location event.
5. Driver location/status information is used for nearby-driver discovery.
---
 
## Distance Calculation
 
The backend uses the **Haversine formula** to calculate the geographical distance between two latitude/longitude coordinates:
 
```
distance = 2R × asin(
    sqrt(
        sin²(Δlat / 2) +
        cos(lat1) × cos(lat2) × sin²(Δlon / 2)
    )
)
```
 
---
 
## Fare Calculation
 
The current fare calculation uses a simple distance-based pricing model:
- Base fare + distance-based pricing
- Fare is calculated automatically during ride creation
---
 
## Database Design
 
The application uses PostgreSQL for persistent storage. Primary entities:
 
- **Driver**
- **Ride**
UUIDs are used as primary identifiers for all entities.
 
---
 
## Ride Lifecycle
 
A ride progresses through the following states:
 
```
CREATED → ASSIGNED → ACCEPTED → STARTED → COMPLETED
```
 
A ride can also be **cancelled** depending on its current state.
 
---
 
## API Documentation
 
The backend provides interactive API documentation via Swagger/OpenAPI.
 
Once the application is running, access it at:
 
```
http://localhost:8081/swagger-ui/index.html
```
 
Swagger can be used to:
- Explore available endpoints
- View request/response schemas
- Test APIs directly
- Understand API parameters
---
 
## Technology Stack
 
| Technology | Purpose | Provider |
|---|---|---|
| Java 21 | Programming language | — |
| Spring Boot | Backend framework | — |
| Spring Data JPA | Database access | — |
| Hibernate | ORM | — |
| PostgreSQL | Relational database | Neon |
| Apache Kafka | Event streaming | Aiven |
| Maven | Build & dependency management | — |
| Swagger/OpenAPI | API documentation | — |
| Docker | Containerization | — |
 
---
 
## Configuration
 
The application requires configuration for:
- PostgreSQL database
- Kafka broker
- Server port
> **Note:** Sensitive credentials should be provided through environment variables or external configuration, and should never be committed to Git.
