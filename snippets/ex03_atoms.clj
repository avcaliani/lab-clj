;;; Exercise 03
;;;     Atoms (in-memory incident incident-store)
;;;     They are Clojure's thread-safe mutable reference.
(ns ex03-atoms
  (:require [clojure.pprint :refer [pprint]]))

;; atom wraps an initial value in a mutable, thread-safe container.
;; The value inside is still immutable, you swap the whole thing atomically*.
;;
;; atomically*
;;      means the read-modify-write happens as one indivisible step,
;;      no other thread can sneak in between the "read" and the "write."
(def incident-store (atom {}))


;; ─── EXERCISES ───────────────────────────────────────────────────────────────
;; 1. Read the atom.
;;    Deref it with @ to get the current value.
;;    Hint: @incident-store — same @ you used for http promises in ex02.
(println "\nEx. 01")
(pprint incident-store)
(pprint @incident-store)


;; 2. Add an incident.
;;    Use swap! to add an incident map to the incident-store, keyed by :id.
;;    Add at least two incidents from different sources.
;;    Python equivalent to -> with lock: incident-store[id] = incident
(defn mock-incident [id name] {:id id
                          :reporter name
                          :source "springfield-nuclear"
                          :severity "low"
                          :description "Kitchen out of Donuts 🍩"})

(println "\nEx. 02")
(swap! incident-store assoc "1" (mock-incident "1" "Homer"))
(swap! incident-store assoc "2" (mock-incident "2" "Lenny"))
(pprint incident-store)


;; 3. Retrieve by id.
;;    Write a fn `get-incident` that takes the incident-store and an id, returns the incident or nil.
;;    Hint: (get @incident-store id)
(defn get-incident [id incidents]
  (when (some? incidents) (get incidents id)))  ;; (some? value) = (not (nil? value))

(println "\nEx. 03")
(pprint (get-incident "0" @incident-store))
(pprint (get-incident "1" @incident-store))


;; 4. List all.
;;    Write a fn `list-incidents` that returns all incidents as a seq of maps (drop the keys).
;;    Hint: (vals @incident-store)
(defn get-incidents [incidents]
  (when (some? incidents) (vals incidents)))

(println "\nEx. 04")
(pprint (get-incidents @incident-store))


;; 5. Filter by source.
;;    Write a fn `by-source` that takes the incident-store and a source string,
;;    returns all incidents from that source.
(defn by-source [source incidents]
  (->> incidents vals (filterv #(= source (:source %)))))

(println "\nEx. 05")
(pprint (by-source "springfield-nuclear" @incident-store))


;; 6. Delete an incident.
;;    Use swap! with dissoc to remove an incident by id.
;;    Hint: (swap! incident-store dissoc id)
(println "\nEx. 06")
(swap! incident-store dissoc "1")
(pprint @incident-store)


;; 7. Challenge — reset vs swap!
;;    reset! sets the atom to a value directly, ignoring the current state.
;;    Use it to clear the incident-store (set it back to {}).
;;    Then verify with @incident-store that it's empty.
;;    Question to think about: when would you prefer reset! over swap!?
(println "\nEx. 07")
(reset! incident-store {:message "this seems dangerous 👀"}) ;; It's possible to change data shape ⚠️
(pprint @incident-store)
