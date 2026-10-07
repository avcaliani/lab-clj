;;; Exercise 06
;;;     Multimethods (ingestion dispatch)
;;;     defmulti/defmethod dispatch on the RESULT of a function you choose.
;;;     Same idea as a Scala sealed-trait + pattern match, but open for extension:
;;;         new source type = new defmethod, no touching existing code.
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

(defmethod ingest :kafka [message]
  {:id (:key message)
   :payload (:value message)
   :source (:source-type message)})

(defmethod ingest :s3 [file]
  {:id (str "s3://" (:bucket file) "/" (:key file))
   :payload (:body file)
   :source (:source-type file)})

(defmethod ingest :jdbc [record]
  {:id (-> record :row :id)
   :payload (-> record :row :name)
   :source (:source-type record)})

(defmethod ingest :default [value]
  {:error "Source type not found ⚠️"
   :source (:source-type value)})

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

;; My understanding on how dispatch works...
;; 1. (ingest msg) calls the "dispatch fn" on the whole msg.
;;    Here it is the keyword :source-type, but any fn works.
;; 2. The returned value (e.g. :kafka) is matched against each defmethod's dispatch value.
;; 3. On a match, that method receives the original msg, :source-type included.
;;    (message, file, record are just parameter names.)
;; 4. No match: falls back to the :default method, or throws if there is none.
