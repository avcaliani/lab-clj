;;; Exercise 05
;;;     Kafka — producer + consumer
;;;     A producer sends Springfield incidents to a topic.
;;;     A consumer reads them back in a separate terminal.
;;;
;;;     Start Kafka first:
;;;       cd snippets/ && docker compose up -d
;;;
;;;     Terminal 1 — run the consumer first so it's ready:
;;;       clojure -M snippets/ex05_kafka_consumer.clj
;;;     Terminal 2 — produce incidents:
;;;       clojure -M snippets/ex05_kafka_producer.clj

(ns ex05-kafka-producer
  (:require [jackdaw.client :as kafka]
            [jackdaw.data   :as jd]
            [cheshire.core  :as json]))

;; jackdaw wraps the Java Kafka client in idiomatic Clojure.
;; Config is a plain map — same keys as Java client properties.

(def topic-config
  {:topic-name         "springfield-incidents"
   :partition-count    1
   :replication-factor 1
   :topic-config       {}})

(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer"    "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer"  "org.apache.kafka.common.serialization.StringSerializer"})

(def sample-incidents
  [{:id "1" :reporter "Homer Simpson" :source "springfield-nuclear" :severity "critical"}
   {:id "2" :reporter "Bart Simpson"  :source "kwik-e-mart"         :severity "low"}
   {:id "3" :reporter "Chief Wiggum"  :source "springfield-pd"      :severity "high"}])


;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Open a producer and send one incident.
;;    kafka/producer returns a closeable client — use `with-open` to auto-close it.
;;    jd/->ProducerRecord builds a record from [topic-config key value].
;;    kafka/send! is async — deref with @ to wait for the ack.
;;    Python analogy: with-open ~ context manager (with producer as p: ...)
;;
;;    (with-open [p (kafka/producer producer-config topic-config)]
;;      @(kafka/send! p (jd/->ProducerRecord topic-config "key" "value")))

;; 2. Send all sample incidents.
;;    Use doseq to iterate and send each one.
;;    Serialize the incident map to JSON with (json/generate-string incident).
;;    Use (:id incident) as the Kafka message key.

;; 3. Add a malformed incident to the batch (missing :severity) and send it too.
;;    The producer doesn't know it's bad — validation is the consumer's job.
;;    Observe that it goes through without error.

;; 4. Challenge — send N random incidents.
;;    Use rand-nth to pick random source, reporter, and severity.
;;    Send 10 incidents and verify they appear in the consumer terminal.
