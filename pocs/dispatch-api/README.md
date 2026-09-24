<div align="center">

# `dispatch-api`

![Clojure](https://img.shields.io/badge/Clojure_1.12.5-5881D8?logo=clojure&logoColor=white)
![Leiningen](https://img.shields.io/badge/Leiningen-4A4A4A)
![DynamoDB](https://img.shields.io/badge/DynamoDB-4053D6?logo=amazondynamodb&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)

![Homer](https://media4.giphy.com/media/3o6MbbT5ctRJeOnPIA/giphy.gif)

Homer keeps causing incidents at the nuclear plant.  
This API ingests and queries incident reports from Springfield sources.

</div>

## Endpoints

**System**

```text
GET  /api/version                     return running version → {"version": "<API_VERSION>"}
```

**Incidents**

```text
POST /api/v1/incidents                validate payload → write to DynamoDB (PK: id)
GET  /api/v1/incidents                scan DynamoDB → return JSON list
GET  /api/v1/incidents/:id            get by PK → return single incident
GET  /api/v1/incidents?source=<src>   query GSI on `source` → return filtered list
```

## Incident Shape

```json
{
  "id": "uuid",
  "reporter": "Homer Simpson",
  "source": "springfield-nuclear",
  "severity": "critical",
  "description": "Donut stuck in reactor panel",
  "ts": "2024-01-03T08:00:00Z"
}
```

## DynamoDB Table

- PK: `id` (UUID)
- GSI (Global Secondary Index): `source-index` on `source` — enables filtered GET by source without a full scan

## Folder Structure

```text
src/dispatch_api/
├── core.clj        ← server startup, -main
├── config.clj      ← env vars, default headers, canned error responses
├── middleware.clj  ← Ring middleware
├── model.clj       ← clojure.spec validation schemas
├── service.clj     ← business logic (pure functions)
├── db.clj          ← DynamoDB operations
└── routes/
    ├── system.clj  ← operational routes under /api (e.g. /api/version)
    └── v1.clj      ← Compojure routes under /api/v1
```

**References**
- [RESTful Clojure Part 3](https://kendru.github.io/restful-clojure/2014/03/01/building-out-the-web-service-restful-clojure-part-3/) — handler/models/db separation
- [Ring Concepts](https://github.com/ring-clojure/ring/wiki/Concepts) — request/response model, informs handler/middleware split
- [Ring Middleware Patterns](https://github.com/ring-clojure/ring/wiki/Middleware-Patterns) — how middleware fits into the layered structure
- [Compojure `context` macro](https://weavejester.github.io/compojure/compojure.core.html) — versioned route prefixing (`/api/v1`)

## Configuration

| Env var       | Default     | Used by            |
|---------------|-------------|--------------------|
| `API_VERSION` | `local-run` | `GET /api/version` |

## Commands

```bash
# Run tests
lein test

# Start the API (default port: 8080)
lein run
lein run 9000  # custom port

# Sanity Check
curl -s http://localhost:8080/api/version

# Fix formatting violations locally
lein cljfmt fix

# Lint
lein clj-kondo --lint src test
```

## Docker

```bash
# Build & Start Containers
docker compose up --build

# Init "Incidents" Table
bash scripts/init-table.sh

# Check memory/CPU usage against the compose limits
docker stats

# Shutdown
docker compose down
```
