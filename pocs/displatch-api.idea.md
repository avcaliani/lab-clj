# `dispatch-api`
> Springfield Emergency Dispatch

Homer keeps causing incidents at the nuclear plant. This API ingests and queries incident reports from Springfield sources.

## Endpoints

```text
POST /incidents                       validate payload → write to DynamoDB (PK: id)
GET  /incidents                       scan DynamoDB → return JSON list
GET  /incidents/:id                   get by PK → return single incident
GET  /incidents?source=<source>       query GSI on `source` → return filtered list
```

## Incident shape

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

## Stack

- Ring + Compojure (HTTP)
- `cognitect/aws-api` (DynamoDB client)
- `amazon/dynamodb-local` via Docker (local dev)

