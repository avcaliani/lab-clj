;;; Exercise 05 — consumer (run this in Terminal 1)
;;;     cd snippets
;;;     clojure -M ex05_kafka_consumer.clj
;;;     Then run ex05_kafka_producer.clj in Terminal 2 to produce messages.

(ns ex05-kafka-consumer
  (:require [jackdaw.client :as kafka]
            [cheshire.core  :as json]
            [clojure.pprint :refer [pprint]]))

(def topics-config
  [{:topic-name "SPRINGFIELD_INCIDENTS_V1"}])

(def consumer-config
  {"bootstrap.servers"  "localhost:9092"
   "group.id"           "lab-clj.ex05-consumer"
   "auto.offset.reset"  "earliest"
   "key.deserializer"   "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 2. Parse JSON.
;; Each record has :key, :value, :topic, :partition, :offset.
(defn print-record [record]
  (println "")
  (pprint {:key (:key record)
           :incident (-> record :value (json/parse-string true))})
  (println ""))

;; 3. Filter inside the consumer.
;;    Only print incidents where :severity is "critical".
;;    Think about where the right place to filter is — before or after parsing?

;; 4. Validate with spec.
;;    Require clojure.spec.alpha and define a minimal ::incident spec inline.
;;    Print "[VALID]" or "[INVALID]" before each incident.
;;    The malformed incident from ex05_kafka_producer.clj exercise 3 should show as invalid.

;; 5. Challenge — count by source.
;;    Use an atom to accumulate a frequency count of incidents by :source.
;;    Print the atom after each poll batch.
;;    Observe it grow as the producer sends more messages.


(println "Consumer Started 🚀")
(println "---------------------")
(println "Press CTRL+C to stop!\n")

;; 1. Poll and print raw records.
;; kafka/poll! returns a seq of records for the given timeout (ms).
(with-open [consumer (kafka/subscribed-consumer consumer-config topics-config)]
  (loop []
    (let [records (kafka/poll consumer 5000)] ; 5 seconds
      (println "Batch Size: " (count records))
      (doseq [rec records]
        (print-record rec)))
    (recur))) ; jump back to loop
