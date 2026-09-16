;;; Exercise 06
;;;     Multimethods (ingestion dispatch)
;;;     defmulti/defmethod dispatch on the RESULT of a function you choose —
;;;     here, a keyword — not just a type. Same idea as a Scala sealed-trait +
;;;     pattern match, but open for extension: new source type = new
;;;     defmethod, no touching existing code.
(ns ex06-multimethod
  (:require [clojure.pprint :refer [pprint]]))

;; ─── SETUP: raw records per source (Springfield incident feeds) ────────────
(def kafka-record {:source-type :kafka :key "springfield-nuclear" :value {:msg "meltdown drill"}})
(def s3-record {:source-type :s3 :bucket "kwik-e-mart-logs" :key "reports/incident-42.json" :body "{...}"})
(def jdbc-record {:source-type :jdbc :table "incidents" :row {:id 1 :name "Homer Simpson"}})
(def unknown-record {:source-type :ftp :whatever "x"})


;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Define the multimethod.
;;    defmulti takes a name and a dispatch fn. Dispatch on :source-type —
;;    keywords are functions, so :source-type works directly as the dispatch fn.
;; TODO: (defmulti ingest ...)


;; 2. Implement a method per source.
;;    defmethod takes the multimethod name, the dispatch VALUE it matches,
;;    then a normal fn body. All three normalize to {:id .. :payload .. :source ..}.
;;    Python analogy: like a dict-of-functions dispatch table, but the compiler/
;;    runtime maintains the table for you and it's open for extension anywhere.
;; TODO :kafka -> :id = key, :payload = value, :source = :kafka
;; TODO :s3    -> :id = key (the s3 key), :payload = {:raw body} (no real JSON
;;                parse needed), :source = :s3
;; TODO :jdbc  -> :id = (:id row), :payload = row, :source = :jdbc

(println "\nEx. 02")
(pprint (ingest kafka-record))
(pprint (ingest s3-record))
(pprint (ingest jdbc-record))


;; 3. Default / unknown type.
;;    :default is the built-in fallback dispatch value — no extra wiring
;;    needed. Throw ex-info so callers get a structured error, not a string.
;; TODO :default -> (throw (ex-info "Unknown source type" {:record record}))

(println "\nEx. 03")
(try
  (ingest unknown-record)
  (catch clojure.lang.ExceptionInfo e
    (println (ex-message e) (ex-data e))))


;; 4. Challenge — a second multimethod, composed.
;;    Write `validate` dispatching on :source (from the NORMALIZED map, not
;;    the raw record) and check required keys per source (e.g. :jdbc payload
;;    must have :id). Compose it with ingest: (validate (ingest record)).
;; TODO: (defmulti validate ...)
;; TODO: defmethod per source

(println "\nEx. 04")
(println (validate (ingest kafka-record)))
(println (validate (ingest s3-record)))
(println (validate (ingest jdbc-record)))
