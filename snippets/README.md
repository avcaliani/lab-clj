<img src="https://clojure.org/images/clojure-logo-120b.png" align="right" height="64px"/>

# Snippets λ

### Exercise 01

Read CSV → Parse / Filter / Change / Group

```bash
clojure -M snippets/ex01_csv_basics.clj
```

### Exercise 02

HTTP Client ↔ Local API Server

`deps.edn` declares the dependencies needed for this exercise:
- `http-kit` - HTTP server and client
- `cheshire` - JSON encode/decode

```bash
# this will make load the deps.edn
cd snippets/

# terminal 1
clojure -M ex02_server.clj

# terminal 2
clojure -M ex02_http.clj
```

> Why not `clj-http`?  
> `clj-http` is a very common HTTP client, but!  
> This exercise runs a local server as well, making the `http-kit` a good fit.

### Exercise 03

Atoms — in-memory incident store

```bash
clojure -M snippets/ex03_atoms.clj
```

### Exercise 04

`clojure.spec` — validate incident data shape

```bash
clojure -M snippets/ex04_spec.clj
```

<details>
<summary>Cost & When to use spec?</summary>

**Cost**  

- Runtime-only checks, no static/compile-time guarantee.
- `s/valid?` walks the spec tree each call, avoid in hot loops.
- Function instrumentation (`s/fdef` + `stest/instrument`) is dev/test only, not prod.

**Adopt:** At system boundaries (parsing external JSON/EDN, API bodies, config).
**Skip**: For internal pure functions and small scripts, same instinct as not
pydantic-validating every intermediate variable.

Refs: [guide](https://clojure.org/guides/spec) · [rationale](https://clojure.org/about/spec)

</details>

### Exercise 05

Kafka — producer + consumer

`deps.edn` declares the dependency needed for this exercise:
- `jackdaw` - idiomatic Clojure wrapper around the Java Kafka client

`docker-compose.yml` declares the local Kafka service, pulled from
[avcaliani/kafka-in-docker](https://hub.docker.com/r/avcaliani/kafka-in-docker):

```bash
# this will make load the docker-compose.yml
cd snippets/
docker compose up -d
```

```bash
# terminal 1 — run the consumer first so it's ready
clojure -M snippets/ex05_kafka_consumer.clj

# terminal 2 — produce incidents
clojure -M snippets/ex05_kafka_producer.clj
```
