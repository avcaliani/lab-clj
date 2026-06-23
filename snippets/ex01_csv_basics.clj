;;; Exercise 01 — CSV basics
;;; Run: clj snippets/ex01_csv_basics.clj
;;;
;;; Dataset: ingestion events from Springfield sources (kwik-e-mart, springfield-nuclear).
 
;; ─── SETUP: read and parse the CSV ──────────────────────────────────────────
;;
;; -> is the thread-first macro: pipes the result into the FIRST arg of each form.
;; Scala analogy: x |> f |> g

(defn read-csv
  [file-name]
  (-> file-name
      slurp
      clojure.string/split-lines
      rest))

;; let binds immutable locals (like val in Scala).
;; [id source ...] destructures the vector — same idea as Scala's unapply.
;; Keywords are functions: (:status row) == (get row :status) == row.status in Scala.

(defn parse-row
  "Parse CSV Row into an Event structure"
  [row]
  (let [att-list (clojure.string/split row #"[,;]")]
    {
     :event_id (nth att-list 0)
     :source   (nth att-list 1)
     :status   (nth att-list 2)
     :bytes    (nth att-list 3)
     :ts       (nth att-list 4)
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

(doseq [x springfield-events] (println x))

;; 2. Filter.
;;    Get only the rows where :status is "ok".
;;    Hint: #(= (:status %) "ok") is an anonymous fn — % is the single arg.

;; 3. Transform.
;;    Add a :kb field (bytes / 1024.0) to each ok row.
;;    Hint: assoc returns a NEW map with a key added, original is untouched.
;;    Scala analogy: case class .copy(kb = bytes / 1024.0)

;; 4. Reduce.
;;    Sum total bytes across all ok rows.
;;    Hint: keywords work as functions, so (map :bytes rows) extracts the field.

;; 5. Group-by.
;;    Group ok rows by :source, then sum bytes per source.
;;    Spark analogy: .groupBy("source").agg(sum("bytes"))
;;    Hint: group-by returns {"source" [rows ...]}. Use (fn [[k v]] ...) to destructure entries.

;; 6. Challenge.
;;    Write a fn `summarize` that takes `rows` and returns:
;;    {:total-events <n>, :ok-events <n>, :error-events <n>, :bytes-by-source {...}}