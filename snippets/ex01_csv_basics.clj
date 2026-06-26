;;; Exercise 01 — CSV basics
;;; Run: clj -M snippets/ex01_csv_basics.clj
;;;
;;; Dataset: ingestion events from Springfield sources (kwik-e-mart, springfield-nuclear).
(ns ex01-csv-basics
  (:require [clojure.string :as str]
            [clojure.pprint :refer [pprint]])
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
  (let [[id, source, status, bytes, ts] (str/split row #"[,;]")]
    {:event-id (Integer/parseInt id)
     :source   source
     :status   status
     :bytes    (Integer/parseInt bytes)
     :ts       (LocalDate/parse ts)
    }))

;; ->> is thread-LAST: result goes into the LAST argument of each form.
;; map/filter/reduce all take the collection last, so ->> is the natural fit.

(def springfield-events
  (->> "./snippets/data/events.csv"
      read-csv
      (mapv parse-row)))


;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Inspect the data.
;;    Print the total number of rows and the first row.

(defn print-list
  [events]
  (doseq [e events] (println e)))

(println "️Springfield Events 🍩")
(print-list springfield-events)

;; 2. Filter.
;;    Get only the rows where :status is "ok".
;;    Hint: #(= (:status %) "ok") is an anonymous fn — % is the single arg.
(defn okay? [event] (= (:status event) "ok"))

(println "️\nOkay Events ✅")
(print-list (filter okay? springfield-events))

;; 3. Transform.
;;    Add a :kb field (bytes / 1024.0) to each ok row.
;;    Hint: assoc returns a NEW map with a key added, original is untouched.
;;    Scala analogy: case class .copy(kb = bytes / 1024.0)

(defn add-size-in-kb
  [event]
  (if (okay? event)
    (assoc event :kb (/ (:bytes event) 1024.0))
    event))

(println "️\nEvents with Size 📏")
(->> springfield-events
     (map add-size-in-kb)
     print-list)

;; 4. Reduce.
;;    Sum total bytes across all ok rows.
;;    Hint: keywords work as functions, so (map :bytes rows) extracts the field.

(println
  "️\n🧮 Total Bytes: "
  (->> springfield-events
       (filter okay?)
       (map :bytes)
       (reduce +)))

;; 5. Group-by.
;;    Group ok rows by :source, then sum bytes per source.
;;    Spark analogy: .groupBy("source").agg(sum("bytes"))
;;    Hint: group-by returns {"source" [rows ...]}. Use (fn [[k v]] ...) to destructure entries.

(defn bytes-by-source
  "group events by source, summing the number of bytes. "
  [events]
  (->> events
       (group-by :source)
       (map (fn [[k v]] {:source k
                         :total-bytes (reduce + (mapv :bytes v))}))))

(println "️\nGrouped Events ✏️")
(->> springfield-events
     (filter okay?)
     bytes-by-source
     print-list)

;; 6. Challenge.
;;    Write a fn `summarize` that takes `rows` and returns:
;;    {:total-events <n>, :ok-events <n>, :error-events <n>, :bytes-by-source {...}}
;;    Note: :bytes-by-source should only include ok rows (same as Task 5).

(def error? (complement okay?)) ;; complement is an alternative to `(not x)`, it inverts the function result

(defn summarize
  [events]
  (let [ok-events (filter okay? events)]
    {:total-events (count events)
     :ok-events (count ok-events)
     :error-events (- (count events) (count ok-events))
     :bytes-by-source (->> events (filter okay?) bytes-by-source)}))

(println "️\nEvents Summary 📊")
(-> springfield-events
    summarize
    pprint)
