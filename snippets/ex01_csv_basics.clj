;;; Exercise 01 — CSV basics
;;; Run: clj -M snippets/ex01_csv_basics.clj
;;;
;;; Dataset: ingestion events from Springfield sources (kwik-e-mart, springfield-nuclear).
(ns ex01-csv-basics
  (:require [clojure.string :as str])
  (:import java.time.LocalDate))

;; ─── SETUP: read and parse the CSV ──────────────────────────────────────────
;; -> is the thread-first macro: pipes the result into the FIRST arg of each form.

(defn read-csv
  [file-name]
  (-> file-name
      slurp
      str/split-lines
      rest)) ;; Remove the Header

(defn parse-row
  "Parse CSV Row into an Event structure"
  [row]
  (let [att-list (str/split row #"[,;]")]
    {
     :event_id (nth att-list 0)
     :source   (nth att-list 1)
     :status   (nth att-list 2)
     :bytes    (Integer/parseInt (nth att-list 3))
     :ts       (LocalDate/parse (nth att-list 4))
    }))

;; ->> is thread-LAST: result goes into the LAST argument of each form.
;; map/filter/reduce all take the collection last, so ->> is the natural fit.

(def springfield-events
  (->> "./snippets/data/events.csv"
      read-csv
      (map parse-row)))


;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Inspect the data.
;;    Print the total number of rows and the first row.

(defn pprint
  [events]
  (doseq [e events] (println e)))

(println "️Springfield Events 🍩")
(pprint springfield-events)

;; 2. Filter.
;;    Get only the rows where :status is "ok".
;;    Hint: #(= (:status %) "ok") is an anonymous fn — % is the single arg.
(defn is-okay? [event] (= (:status event) "ok"))

(println "️\nOkay Events ✅")
(pprint (filter is-okay? springfield-events))

;; 3. Transform.
;;    Add a :kb field (bytes / 1024.0) to each ok row.
;;    Hint: assoc returns a NEW map with a key added, original is untouched.
;;    Scala analogy: case class .copy(kb = bytes / 1024.0)

(defn add-random-size-info
  [event]
  (if is-okay?
    (assoc event :kb (/ (:bytes event) 1024))
    event))

(println "️\nEvents with Size 📏")
(->> springfield-events
     (map add-random-size-info)
     pprint)

;; 4. Reduce.
;;    Sum total bytes across all ok rows.
;;    Hint: keywords work as functions, so (map :bytes rows) extracts the field.

(println
  "️\n🧮 Total Bytes: "
  (->> springfield-events
       (filter is-okay?)
       (map #(:bytes %1))
       (reduce +)))

;; 5. Group-by.
;;    Group ok rows by :source, then sum bytes per source.
;;    Spark analogy: .groupBy("source").agg(sum("bytes"))
;;    Hint: group-by returns {"source" [rows ...]}. Use (fn [[k v]] ...) to destructure entries.
(println "️\nGrouped Events ✏️ ")
(->> springfield-events
     (filter is-okay?)
     (group-by :source)
     (map (fn [[k v]] {:source k
                       :total-bytes (reduce + (map :bytes v))}))
     pprint)

;; 6. Challenge.
;;    Write a fn `summarize` that takes `rows` and returns:
;;    {:total-events <n>, :ok-events <n>, :error-events <n>, :bytes-by-source {...}}