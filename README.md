# Buuking.com

## Hotel Room Reservation System

This is a simple hotel room reservation system project designed to demonstrate skills in handling exceptions effectively
and implementing OpenTelemetry reporting for monitoring and observability. The project uses Docker Compose to
orchestrate containers, Grafana for data visualization, OpenTelemetry for collecting metrics and Prometheus for storing
and querying metrics.

## Features

* Room Reservation: Allows you to reserve hotel rooms.
* Room Management: Add, edit and remove available rooms.
* Reporting and Monitoring: Implements OpenTelemetry reports for system monitoring and observability

## Technologies Used

* Docker Compose: For orchestration and management of containers.
* Grafana: For visualizing metrics and data.
* OpenTelemetry: For metrics collection and tracking.
* Tempo: For tracing.
* Prometheus: For storing and querying metrics.
* Loki: For log management.
* Spring Boot: For fast development of java web applications.
* Spring Data: For facilitating the data access with JPA.
* Spring Validation: For validating request inputs
* Lombok: For reducing boiler plates
* Mapstruct: For Handling 1:1 object conversions.
* MySql: For storing relational data.

## Project Structre

**docker-compose.yml**:  Docker Compose definition to run the all app and its dependencies.</br>
**docker-compose-local.yml**:  Docker Compose definition to run only the mysql database.</br>
**./config/***: Configurations for Grafana, Loki, Prometheus and Tempo.</br>
**src/**: Source code of hotel room reservation system.

## Requirements

- Docker and Docker Compose

Or

- Java Development Kit (JDK) 21, Maven and Docker Vompose

## Running the application

### Option with Docker

> docker compose -f docker-compose.yml up -d

### Option with Java and Maven

> docker compose -f docker-compose-local.yml up -d

> mvn spring-boot:run

## Services url

| Service              | URL                                   | Running option |
|----------------------|---------------------------------------|----------------|
| Buuking.com swagger  | localhost:8080/                       | Always         |
| Buuking.com actuator | localhost:8080/actuator               | Always         |
| Buuking.com health   | http://localhost:8080/actuator/health | Always         |
| Grafana              | localhost:3000                        | Only Docker    |

<img align="center" alt="Myka-Postman" height="30" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/postman/postman-original.svg" />[Download Postman Collection](https://github.com/mykadias/buuking-dot-com/blob/main/Buuking.com.postman_collection.json)

