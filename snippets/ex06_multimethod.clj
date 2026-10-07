;;; Exercise 06
;;;     Multimethods (ingestion dispatch)
;;;     defmulti/defmethod dispatch on the RESULT of a function you choose —
;;;     here, a keyword — not just a type. Same idea as a Scala sealed-trait +
;;;     pattern match, but open for extension: new source type = new
;;;     defmethod, no touching existing code.
(ns ex06-multimethod
  (:require [clojure.pprint :refer [pprint]]))

;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Define the multimethod.
;;    defmulti takes a name and a dispatch fn, dispatch on :source-type
;;
;; 2. Implement a method per source.
;;    defmethod takes the multimethod name, the dispatch VALUE it matches, then a normal fn body.
;;    All three normalize to {:id .. :payload .. :source ..}.
;;
(defmulti ingest :source-type)

(defmethod ingest :kafka [message] {:id (:key message)
                                   :payload (:value message)
                                   :source :kafka})

(defmethod ingest :s3 [file] {:id (str "s3://" (:bucket file) "/" (:key file))
                              :payload (:body file)
                              :source :s3})

(defmethod ingest :jdbc [record] {:id (-> record :row :id)
                                  :payload (-> record :row :id)
                                  :source :jdbc})

(println "\nEx. 02")

;; Kafka
(pprint (ingest {:source-type :kafka
                 :key "springfield-nuclear"
                 :value {:msg "meltdown drill"}}))
;; S3
(pprint (ingest {:source-type :s3
                 :bucket "kwik-e-mart-logs"
                 :key "reports/incident-42.json"
                 :body "{...}"}))
;; JDBC
(pprint (ingest {:source-type :jdbc
                 :table "incidents"
                 :row {:id 1
                       :name "Homer Simpson"}}))

;; Non-Mapped Source Type
(pprint (ingest {:source-type :ftp
                 :file "/root/springfield-dounts-request.csv"}))
