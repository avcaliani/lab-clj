;;; Exercise 05 — consumer (run this in Terminal 1)
;;;     clojure -M snippets/ex05_kafka_consumer.clj
;;;     Then run ex05_kafka_producer.clj in Terminal 2 to produce messages.

(ns ex05-kafka-consumer
  (:require [jackdaw.client :as kafka]
            [jackdaw.data   :as jd]
            [cheshire.core  :as json]))

(def topic-config
  {:topic-name         "springfield-incidents"
   :partition-count    1
   :replication-factor 1
   :topic-config       {}})

(def consumer-config
  {"bootstrap.servers"  "localhost:9092"
   "group.id"           "dispatch-consumer"
   "auto.offset.reset"  "earliest"
   "key.deserializer"   "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

;; kafka/poll! returns a seq of records for the given timeout (ms).
;; Each record has :key, :value (both strings), :topic, :partition, :offset.
;; loop/recur keeps polling indefinitely — Ctrl+C to stop.


;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Poll and print raw records.
;;    Open a consumer, subscribe to the topic, and poll in a loop.
;;    Print the raw :value of each record.
;;    Hint:
;;    (with-open [c (kafka/consumer consumer-config topic-config)]
;;      (kafka/subscribe c [topic-config])
;;      (loop []
;;        (doseq [r (kafka/poll! c 1000)]
;;          (println (:value r)))
;;        (recur)))

;; 2. Parse JSON.
;;    Replace (println (:value r)) with JSON parsing.
;;    Use (json/parse-string (:value r) true) to get a keywordized map.
;;    Pretty-print each incident.

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
