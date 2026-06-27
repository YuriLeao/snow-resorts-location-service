# snow-resorts-location-service

Real-time location microservice for Snow Resorts: WebSocket STOMP over `/ws`, Redis
Pub/Sub fanout of friend positions (enriched with cached `avatarUrl`), and groups.

- **Port:** 8084
- **DB schema:** `location`
- **Shared libs:** `com.snowresorts:security-lib` (from GitHub Packages)
- **Realtime:** WebSocket STOMP + Redis (ElastiCache in prod)

## Build & test

Requires a `github` server credential in `~/.m2/settings.xml` (see
[`settings.xml.example`](settings.xml.example)) to resolve the shared libraries.

```bash
./mvnw clean verify
./mvnw spring-boot:run    # `local` profile against the local Docker stack
```

Bring up Postgres/Redis/MinIO from [`snow-resorts-infra`](https://github.com/yurileao/snow-resorts-infra) (`make dev`).

## CI/CD

See [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml). Requires repo secret
`AWS_DEPLOY_ROLE_ARN`.
