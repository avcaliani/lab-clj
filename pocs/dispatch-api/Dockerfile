# https://hub.docker.com/_/clojure
FROM clojure:temurin-17-lein-2.12.0-bookworm-slim

# Docker Build Args 👇
ARG PORT=8080

# Env Variables 🌴
ENV API_PORT="${PORT}"
ENV API_VERSION="0.0.1-alpha"
ENV API_ENVIRONMENT="docker"
EXPOSE "${API_PORT}"

# Install Deps 👇
WORKDIR /app
COPY project.clj .
RUN lein deps

# Copy Project Files + Build 👇
COPY src ./src
RUN lein uberjar

# Run 🚀
#   `exec` makes java the main process, so `docker compose down` stops it cleanly.
#   The glob keeps the jar path independent of API_VERSION (which only feeds /api/version).
CMD exec java -jar target/uberjar/*-standalone.jar "${API_PORT}"
