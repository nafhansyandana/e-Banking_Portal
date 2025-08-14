# eBanking Transactions Service

Microservice (Java 21, Spring Boot) to display a list of monthly transactions with pagination, consumption from Kafka, and currency conversion.

## Tech Stack
- Spring Boot (web, security, oauth2 resource server, kafka, validation, actuator)
- springdoc-openapi
- Maven
- Docker
- Testcontainers, CircleCI, Kubernetes/Openshift

## Run
```bash
./mvnw spring-boot:run
# Docker:
docker build -t ebanking-transactions-service:0.1.0 .
docker run --rm -p 8080:8080 ebanking-transactions-service:0.1.0
```