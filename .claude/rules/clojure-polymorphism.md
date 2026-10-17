---
paths:
  - "**/*.clj"
---

<img src="https://clojure.org/images/clojure-logo-120b.png" align="right" height="64px"/>

# Clojure Polymorphism

Jump to: [Multimethods](#multimethods) · [Protocols](#protocols)

## Multimethods

`defmulti` + `defmethod` = route a call by a value you pick, not just type.
Python: `functools.singledispatch`, but the dispatch value can be anything.

**Use:** many variants of one op, picked by a field (e.g. `:source-type`), open to new cases.
**Skip:** few fixed cases → plain `cond`/`case`.

```clojure
(defmulti connect :db-type)
(defmethod connect :postgres [cfg] (str "pg conn: " (:host cfg)))
(defmethod connect :dynamodb [cfg] (str "ddb conn: " (:table cfg)))

(connect {:db-type :postgres :host "localhost"})  ; => "pg conn: localhost"
```

**Exercise:** [`snippets/ex06_multimethod.clj`](../../snippets/ex06_multimethod.clj), ingestion dispatch by source type.

---

## Protocols

Dispatch on the type of the first arg only, no other dispatch logic.  
Faster than multimethods. 
Also how you implement Java interfaces.

**Use:** dispatch is purely by type, and it's a hot path.  
**Skip:** dispatch key isn't a type (e.g. `:db-type` keyword) → multimethod instead.

```clojure
(defprotocol DbConn
  (connect [this]))

(defrecord Postgres [host]
  DbConn
  (connect [this] (str "pg conn: " host)))

(defrecord DynamoDb [table]
  DbConn
  (connect [this] (str "ddb conn: " table)))

(connect (->Postgres "localhost"))  ; => "pg conn: localhost"
```
