;;; Exercise 03 — Atoms (in-memory incident store)
;;; Run: clj -M snippets/ex03_atoms.clj
;;;
;;; Atoms are Clojure's thread-safe mutable reference.
;;; You'll use this pattern in the PoC before DynamoDB is wired up.
;;; Scala analogy: AtomicReference[Map[String, Incident]]
(ns ex03-atoms
  (:require [clojure.pprint :refer [pprint]]))

;; atom wraps an initial value in a mutable, thread-safe container.
;; The value inside is still immutable — you swap the whole thing atomically.

(def store (atom {}))

;; swap! applies a fn to the current value and sets the result.
;; Scala analogy: ref.updateAndGet(current -> updatedFn(current))
;;
;; (swap! store assoc id incident)
;; is equivalent to: store = store.updated(id, incident)  — but atomic.


;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Read the atom.
;;    Deref it with @ to get the current value.
;;    Hint: @store — same @ you used for http promises in ex02.

;; 2. Add an incident.
;;    Use swap! to add an incident map to the store, keyed by :id.
;;    Add at least two incidents from different sources.

;; 3. Retrieve by id.
;;    Write a fn `get-incident` that takes the store and an id, returns the incident or nil.
;;    Hint: (get @store id)

;; 4. List all.
;;    Write a fn `list-incidents` that returns all incidents as a seq of maps (drop the keys).
;;    Hint: (vals @store)

;; 5. Filter by source.
;;    Write a fn `by-source` that takes the store and a source string,
;;    returns all incidents from that source.

;; 6. Delete an incident.
;;    Use swap! with dissoc to remove an incident by id.
;;    Hint: (swap! store dissoc id)

;; 7. Challenge — reset vs swap!
;;    reset! sets the atom to a value directly, ignoring the current state.
;;    Use it to clear the store (set it back to {}).
;;    Then verify with @store that it's empty.
;;    Question to think about: when would you prefer reset! over swap!?
