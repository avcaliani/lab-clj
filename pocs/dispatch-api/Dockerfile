# https://hub.docker.com/_/clojure
FROM clojure:temurin-17-lein-2.12.0-bookworm-slim

# Docker Build Args 👇
ARG PORT=8080

# Env Variables 🌴
ENV API_PORT="${PORT}"
ENV API_VERSION="0.0.1"
EXPOSE "${API_PORT}"

# Install Deps 👇
WORKDIR /app
COPY project.clj .
RUN lein deps

# Copy Project Files + Build 👇
COPY src ./src
RUN lein uberjar

# Run 🚀
CMD java -jar \
    "target/uberjar/dispatch-api-${API_VERSION}-alpha-standalone.jar" \
    "${API_PORT}"
