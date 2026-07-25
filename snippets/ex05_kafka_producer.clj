;;; Exercise 05
;;;     Kafka — producer + consumer
;;;     A producer sends Springfield incidents to a topic.
;;;     A consumer reads them back in a separate terminal.
;;;
;;;     Start Kafka first:
;;;       cd snippets/ && docker compose up -d
;;;
;;;     Terminal 1 — run the consumer first so it's ready:
;;;       clojure -M ex05_kafka_consumer.clj
;;;     Terminal 2 — produce incidents:
;;;       clojure -M ex05_kafka_producer.clj
(ns ex05-kafka-producer
  (:require [jackdaw.client :as kafka]
            [clojure.pprint :refer [pprint]]
            [cheshire.core  :as json]))

;; ─── EXERCISES ───────────────────────────────────────────────────────────────
(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer"    "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer"  "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"
   "client.id" "lab-clj.ex05-producer"
   "max.block.ms" (-> 5 (* 1000) str)}) ; 5 seconds timeout

(def sample-incidents
  [{:id "1" :reporter "Homer Simpson" :source "springfield-nuclear" :severity "critical"}
   {:id "2" :reporter "Homer Simpson" :source "springfield-nuclear" :severity "critical"}
   {:id "3" :reporter "Bart Simpson"  :source "kwik-e-mart"         :severity "low"}
   {:id "4" :reporter "Chief Wiggum"  :source "springfield-pd"      :severity "high"}])

(def incorrect-incidents
  [{:id "AEF21A28" :reporter "Barney Gumble" :source "moe's tavern" }
   {:id "5" :reporter "Barney Gumble" :source "moe's tavern"}
   {:id "6" :reporter "Barney Gumble" :source "moe's tavern" :hobby "Drink Beer 🍺"}])


;; Notes - Producing Messages
;; kafka/produce! is async — deref with @ to wait for the ack
;; https://cljdoc.org/d/fundingcircle/jackdaw/0.9.12/doc/jackdaw-client-api#producing"
(defn send-msg!
  [producer incident]
  (let [topic {:topic-name "SPRINGFIELD_INCIDENTS_V1"}
        msg-key (:source incident)
        response (kafka/produce! producer topic msg-key (json/generate-string incident))]
    (try
      (pprint @response)
      (catch Exception e (println "caught exception: " (.getMessage e))))))


;; kafka/producer returns a closeable client,
;; use `with-open` to auto-close it
(with-open [producer (kafka/producer producer-config)]

  ;; 1. Open a producer and send one incident.
  (println "\nEx. 01")
  (send-msg! producer (first sample-incidents))

  ;; 2. Send all sample incidents.
  ;;    Use doseq to iterate and send each one.
  ;;    Serialize the incident map to JSON.
  ;;    Use the incident source as Kafka message key.
  (println "\nEx. 02")
  (doseq [incident sample-incidents]
    (send-msg! producer incident))

  ;; 3. Add a malformed incident to the batch (missing :severity) and send it too.
  ;;    The producer doesn't know it's bad — validation is the consumer's job.
  ;;    Observe that it goes through without error.
  (println "\nEx. 03")
  (doseq [incident incorrect-incidents]
      (send-msg! producer incident)))
