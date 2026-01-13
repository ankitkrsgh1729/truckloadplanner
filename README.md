# SmartLoad Optimization API

REST API for selecting the optimal combination of orders for a truck while respecting weight, volume, hazmat, and route compatibility constraints. Stateless, in-memory only.

## How to run

```bash
git clone <your-repo>
cd truckloadplanner   # project root
docker compose up --build
# Service will be available at http://localhost:8080
```

### Using Maven directly

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## Health check

```bash
curl http://localhost:8080/actuator/health
```

## Example request

```bash
curl -X POST http://localhost:8080/api/v1/load-optimizer/optimize \
  -H "Content-Type: application/json" \
  -d @sample-request.json
```

## Notes

- Dynamic Programming with bitmask (O(2^n × n)) for n ≤ 22 orders.
- Hazmat isolation: compares best single hazmat vs. best non-hazmat set.
- Returns 400 for invalid input and 413 when more than 22 orders are submitted.
