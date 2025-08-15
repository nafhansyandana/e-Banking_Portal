# eBanking Transactions Service

## 1. Overview
`ebanking-transactions-service` is a Spring Boot service that provides API to read user transactions from Kafka, store them in an in-memory store, and return them through REST endpoints. The service also supports currency conversion using external exchange rates.

---

## 2. Main Features
- **Integrasi Kafka** (consuming `TransactionEvent`)
- **In-memory store** untuk penyimpanan transaksi per user & bulan
- **Paging & filter** based on IBAN
- **Calculation of total debit/credit per page**
- **External exchange rate integration** (stub/dev or real)
- **JWT Authentication**
- **Docker & Kubernetes manifest**
- **Pipeline CI/CD** with CircleCI

---

## 3. API Endpoint
- `GET /transactions?month=YYYY-MM&page={n}&size={n}&baseCurrency={CUR}`
  - Auth: Bearer JWT
  - Query Params:
    - `month`: `YYYY-MM`
    - `page`: page index
    - `size`: number of items per page
    - `baseCurrency`: reference currency for conversion
  - Response: transaksi + summary debit/credit

---

## 4. Running it local way
### 4.1. With Maven
```bash
mvn spring-boot:run
```

### 4.2. With Docker
```bash
docker build -t ebanking-transactions-service:local .
docker run -p 8080:8080 \
  -e APP_SECRET_KEY=supersecret \
  -e EXTERNAL_RATES_URL=http://stub-rates \
  ebanking-transactions-service:local
```

### 4.3. JWT Token Dev
Use this token when accessing the API:
```bash
export TOKEN=<JWT-dev>
curl -H "Authorization: Bearer $TOKEN"   "http://localhost:8080/transactions?month=2020-10&page=0&size=50&baseCurrency=EUR"
```

---

## 5. Deployment to Minikube
```bash
minikube start
docker build -t ebanking-transactions-service:local .
minikube image load ebanking-transactions-service:local

kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl get pods
```

---

## 6. CI/CD with CircleCI
- Configuration files are located in `.circleci/config.yml`
- Pipeline:
  - Build & test Java project
  - Build Docker image
- Run pipeline via the CircleCI UI after pushing to the GitHub repository

---

## 7. Architecture (C4 Model - Level 1 & 2)
### 7.1. C4 Context Diagram
```
[ User ] ---> [ eBanking Transactions Service ] ---> [ External Rates Service ]
                               |
                               v
                          [ Kafka Broker ]
```

### 7.2. Container Diagram
- **Spring Boot App**: Handles REST API, Kafka consumption, and debit/credit calculation.
- **Kafka**: Provides event `TransactionEvent`.
- **External Rates Service**: Returns the conversion rate (stub/dev/real).
- **Kubernetes**: Running service + config + secret.

---

## 8. Model Data
### 8.1. TransactionEvent
```json
{
  "id": "uuid",
  "customerId": "string",
  "accountIban": "string",
  "valueDate": "YYYY-MM-DD",
  "amount": {
    "currency": "string",
    "amount": number
  },
  "description": "string"
}
```
