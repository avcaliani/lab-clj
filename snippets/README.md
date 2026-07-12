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

No extra deps — spec is built into Clojure.

```bash
clojure -M snippets/ex04_spec.clj
```
