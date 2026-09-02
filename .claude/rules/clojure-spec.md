---
paths:
  - "**/*.clj"
---

# Clojure Spec

`clojure.spec`: describes data/function shapes with predicates.
One spec = validation + parsing + test-data generation.

**Cost**

- Runtime-only checks, no static/compile-time guarantee.
- `s/valid?` walks the spec tree each call, avoid in hot loops.
- Function instrumentation (`s/fdef` + `stest/instrument`) is dev/test only, not prod.

**Adopt:** At system boundaries (parsing external JSON/EDN, API bodies, config).
**Skip:** For internal pure functions and small scripts, same instinct as not pydantic-validating every intermediate variable.

## Best practices: where to create specs?

1. Co-locate. Spec goes in the same namespace, right above the thing it validates.
2. Split out only if reused across many namespaces, or to avoid circular requires. Name it `myapp.specs.user`.
3. Group by domain (`specs.user`, `specs.order`), not one giant `specs.clj`.
4. Keep specs namespaces lightweight, no heavy deps, since everything requires them.

## Code Snippets

Defining and using specs:

```clojure
(ns myapp.user
  (:require [clojure.spec.alpha :as s]))

(s/def ::id uuid?)
(s/def ::email (s/and string? #(re-matches #".+@.+" %)))
(s/def ::age (s/int-in 0 130))

(s/def ::user (s/keys :req-un [::id ::email]
                       :opt-un [::age]))

(defn adult? [user]
  (>= (:age user 0) 18))

(s/fdef adult?
  :args (s/cat :user ::user)
  :ret boolean?)

(s/valid? ::user {:id (random-uuid) :email "a@b.com"}) ; => true
(s/explain ::user {:id "not-a-uuid" :email "a@b.com"}) ; prints a human-readable failure reason
```

## Alternative: Plumatic/Prismatic Schema

[Plumatic Schema](https://github.com/plumatic/schema) is an older, still widely used alternative for the same problem: describing and validating data shapes.
The core difference is *how* the shape is represented.

```clojure
(require '[schema.core :as s])

(s/defschema User
  {:id    s/Uuid
   :email s/Str
   (s/optional-key :age) s/Int})

(s/validate User {:id (java.util.UUID/randomUUID) :email "a@b.com"}) ; throws if invalid
```

## References

- [clojure.org/guides/spec](https://clojure.org/guides/spec) — official getting-started guide
- [github.com/clojure/spec.alpha](https://github.com/clojure/spec.alpha) — source repository
- [github.com/plumatic/schema](https://github.com/plumatic/schema) — Schema source repository
