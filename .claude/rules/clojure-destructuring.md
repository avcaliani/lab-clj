---
paths:
  - "**/*.clj"
---

<img src="https://clojure.org/images/clojure-logo-120b.png" align="right" height="64px"/>

# Clojure Destructuring

Jump to: [Vectors](#vectors-like-tuple-unpacking) · [Maps](#maps-like-pulling-keys-out-of-a-dict) · [Function Params](#function-params)

A pattern on the left that mirrors the shape of the data on the right.  
Pure sugar: nothing new happens at runtime.  
Works anywhere Clojure binds names: `let`, `defn`/`fn` params, `loop`, `for`, `doseq`.

```clojure
;; manual
(defn greet [user] (str (:name user) " from " (:city user)))

;; destructured in the param vector
(defn greet [{:keys [name city]}] (str name " from " city))
```

**Rule of thumb:** names sit where the values are.

---

## Vectors (like tuple unpacking)

| data | pattern | Python |
|---|---|---|
| `[1 2 3]` | `[a b c]` | `a, b, c = xs` |
| `[1 2 3 4]` | `[a b & rest]` | `a, b, *rest = xs` |
| `[1 2 3]` | `[a _ c]` | `a, _, c = xs` |
| `[1 2 3]` | `[a :as all]` | `a = xs[0]`, keep whole `xs` too |

---

## Maps (like pulling keys out of a dict)

| data | pattern | Python |
|---|---|---|
| `{:id 1 :n "x"}` | `{:keys [id n]}` | `id, n = d["id"], d["n"]` |
| `{}` | `{:keys [id] :or {id 0}}` | `d.get("id", 0)` |
| `{:id 1}` | `{uid :id}` | `uid = d["id"]` (rename) |
| `{"id" 1}` | `{:strs [id]}` | string keys (raw JSON) |
| `{:id 1}` | `{:keys [id] :as m}` | keep whole dict too |
| `{:a {:b 1}}` | `{{:keys [b]} :a}` | `d["a"]["b"]` |
| `{:a [1 2]}` | `{[x y] :a}` | `x, y = d["a"]` |

---

## Function Params

```clojure
;; map arg
(defn save [{:keys [id table]}] ...)

;; vector arg
(defn dist [[x1 y1] [x2 y2]] ...)

;; keyword args: (connect :host "h" :port 5432), like **kwargs
(defn connect [& {:keys [host port] :or {port 5432}}] ...)

;; in a callback: each element of the seq is a map
(map (fn [{:keys [id]}] id) rows)

;; map entries are [k v] pairs
(for [[k v] {:a 1 :b 2}] [k (inc v)])   ; like `for k, v in d.items()`
```

> [!WARNING]
> **Gotchas**
>
> - Missing key gives `nil`, not an error (like `d.get`, not `d[...]`).
> - `:or` only fires when the key is **missing**, not when it is present with value `nil` or `false`.
> - `:keys` needs keyword keys. Raw JSON with string keys needs `:strs`.
> - `:keys [name]` shadows `clojure.core/name` inside that body. Rename with `{n :name}` if you need both.
> - Destructuring does not validate shape. Use `spec` at the boundary.
> - Prefer destructuring in the param vector over `(:k m)` calls in the body when you use 2+ keys. It documents what the function needs.

> [!TIP]
> **Reading a Pattern**  
> Read it as "what data does this expect?":  
> `[{:keys [id]} & more]` = a seq whose first item is a map with `:id`, plus the rest.
