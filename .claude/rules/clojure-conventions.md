---
paths:
  - "**/*.clj"
---

<img src="https://clojure.org/images/clojure-logo-120b.png" align="right" height="64px"/>

# Clojure Conventions

Jump to: [Naming](#naming) · [Namespaces](#namespace--file-path-mapping) · [Functions](#functions) · [Data](#data) · [Idioms](#idioms-that-trip-up-newcomers) · [def vs defn](#def-vs-defn) · [References](#references)

## Naming

| Thing           | Convention                           | Example              |
|-----------------|--------------------------------------|----------------------|
| Namespaces      | `kebab-case`                         | `my-app.data-loader` |
| Source files    | `snake_case` (maps to namespace)     | `data_loader.clj`    |
| Functions/Vars  | `kebab-case`                         | `parse-csv-row`      |
| Predicates      | trailing `?`, returns `true`/`false` | `valid?`, `empty?`   |
| Side-effect fns | trailing `!` (IO, mutation, throws)  | `save-row!`, `swap!` |
| Private Vars    | `defn-` or `^:private`, no prefix    | `helper-fn`          |
| Constants       | `kebab-case`                         | `max-retries`        |
| Dynamic vars    | `*earmuffs*` (+ `^:dynamic`)         | `*db-conn*`          |
| Protocols/types | `PascalCase`                         | `DataSource`         |

---

## Namespace → File Path Mapping

The namespace `my.data-loader` maps to `src/my/data_loader.clj`.
Dashes in namespaces → underscores in filenames.
This is required by the compiler, not optional.

---

## Namespace Structure

```clojure
(ns my.data-loader
  (:require [clojure.string :as str]
            [clojure.data.csv :as csv]))
```

Alias `require`d namespaces with short, conventional aliases: `str`, `io`, `edn`, `json`, `log`.

---

## Functions

- Prefer many small, pure functions over large stateful ones.
- Argument order: **collection last** (so it composes with `->>`), context/config first.
- Use `->` and `->>` threading macros instead of nested calls.

---

## Data

- Prefer plain maps over records or objects.
- Use **keywords** as map keys: `:user-id`, not `"user-id"`.
- Namespace your keywords when sharing data across contexts: `:user/id`.

---

## Idioms That Trip Up Newcomers

```clojure
;; good: threading
(->> rows
     (filter valid?)
     (map parse-row)
     (into []))

;; bad: nested
(into [] (map parse-row (filter valid? rows)))

;; good: destructuring in fn args
(defn process [{:keys [id name]}] ...)

;; avoid: def inside defn — use let
(defn foo []
  (let [x (compute)] ...))
```

The biggest mindset shift from Python: **reach for `map`, `filter`, `reduce`, and threading macros first** — not loops.

---

## `def` vs `defn`

- `def` evaluates **once** at load time — the value is fixed.
- `defn` evaluates **every time it is called**.
- `defn-` (trailing `-`) defines a private function.

```clojure
(def t0 (System/currentTimeMillis))   ; captured once
(defn t1 [] (System/currentTimeMillis)) ; fresh value each call
```

---

## References

- [The Clojure Style Guide](https://guide.clojure.style/) — community-maintained, comprehensive conventions
- [clojure.org: Learn Clojure](https://clojure.org/guides/learn/clojure) — official intro, covers idioms and data model
- [Clojure for the Brave and True](https://www.braveclojure.com/) — beginner-friendly book, free online; good on threading and destructuring
- [Clojure Distilled](https://yogthos.net/ClojureDistilled.html) — short essay on idiomatic Clojure thinking
- [Weird Characters Guide](https://clojure.org/guides/weird_characters) — reference for all the `#`, `@`, `!`, `?` symbols you'll encounter
