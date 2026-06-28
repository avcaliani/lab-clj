---
paths:
  - "**/*.clj"
---

# Clojure Concepts

## Pure Functions

A pure function computes its return value solely from its arguments — no hidden state, no side effects, same input always yields same output.

**Impure (result depends on time of day):**

```scala
// Scala
def sillyCalculation(a: Int, b: Int): Int = {
  val now = LocalTime.now()
  if (now.getHour() < 12) a * b else a + b
}
```

```clojure
;; Clojure equivalent — still impure
(defn silly-calculation [a b]
  (let [now (LocalTime/now)]
    (if (< (.getHour now) 12) (* a b) (+ a b))))
```

**Pure (caller passes the time in):**

```clojure
(defn pure-silly-calculation [a b time]
  (if (< (.getHour time) 12) (* a b) (+ a b)))

(pure-silly-calculation 2 3 (LocalTime/now))
```

The fix is always the same: push impure inputs (time, randomness, I/O) to the call site and pass them as arguments.

## EDN

EDN (Extensible Data Notation) is Clojure's native data format.  
Think JSON with richer types.

```clojure
{:id         #uuid "69E595F7-2A6F-41C6-95AE-E6FDF561BBAA"
 :customer   {:id        #uuid "69E595F7-2A6F-41C6-95AE-E6FDF561BBAA"
               :nickname  "joe"
               :tags      ["user" "free"]
               :birthdate #inst "1997-04-09"}
 :attempts   1
 :score      0.75
 :created-at #inst "2024-07-23T19:27:46.935318Z"}
```

Key differences from JSON:

- Keys are keywords (`:id`), not strings
- Tagged literals: `#uuid` for UUIDs, `#inst` for timestamps
- Sets: `#{1 2 3}`
- No trailing commas needed (whitespace-separated)

More: https://github.com/edn-format/edn

## Conditionals — `if` x `when` x `cond`

```clojure
;; if — Two branches, true and false.
(if (> x 0) "pos" "neg")

;; when — Only care about the true branch. 
;;        Returns `nil` if false.
(when (> x 0) "pos")

;; cond — 3+ conditions, like an `elif` chain.
(cond
  (> x 0) "pos"
  (< x 0) "neg"
  :else   "zero")
```

---

## Recursion

### Direct Recursion

Simple but **stack unsafe**, blows up on large inputs 💣

```clojure
(defn sum [lst]
  (if (empty? lst)
    0
    (+ (first lst) (sum (rest lst)))))
```

### `recur`

Tail-call safe. Must be in tail position. Needs an accumulator.

```clojure
(defn sum [lst acc]
  (if (empty? lst)
    acc
    (recur (rest lst) (+ acc (first lst)))))

(sum [1 2 3] 0)  ; => 6
```

### `loop/recur`
Same as `recur` but self-contained.

```clojure
(loop [lst [1 2 3] acc 0]
  (if (empty? lst)
    acc
    (recur (rest lst) (+ acc (first lst)))))  ; => 6
```

| | Stack safe? | When |
|---|---|---|
| direct recursion | ❌ | small/bounded depth |
| `recur` | ✅ | tail-recursive named fn |
| `loop/recur` | ✅ | self-contained |

> Prefer `reduce` / `map` / `filter` over manual recursion.