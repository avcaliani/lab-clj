;;; Exercise 05 — consumer (run this in Terminal 1)
;;;     cd snippets
;;;     clojure -M ex05_kafka_consumer.clj
;;;     Then run ex05_kafka_producer.clj in Terminal 2 to produce messages.

(ns ex05-kafka-consumer
  (:require [cheshire.core  :as json]
            [clojure.pprint :refer [pprint]]
            [clojure.spec.alpha :as s]
            [colors :refer [tag]]
            [ex04-spec]
            [jackdaw.client :as kafka]))

(def topics-config
  [{:topic-name "SPRINGFIELD_INCIDENTS_V1"}])

(def consumer-config
  {"bootstrap.servers"  "localhost:9092"
   "group.id"           "lab-clj.ex05-consumer"
   "auto.offset.reset"  "earliest"
   "key.deserializer"   "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 4. Validate with spec.
;;    Require clojure.spec.alpha and define a minimal ::incident spec inline.
;;    Print "[VALID]" or "[INVALID]" before each incident.
;;    The malformed incident from ex05_kafka_producer.clj exercise 3 should show as invalid.
(defn assert-valid! [incident]
  (let [invalid? (not (s/valid? :ex04-spec/incident incident))]
    (when invalid?
      (throw (Exception. (s/explain-str :ex04-spec/incident incident))))
    incident))

;; 2. Parse JSON.
;; Each record has :key, :value, :topic, :partition, :offset.
(defn parse-incident [record]
  (let [payload (:value record)]
    (try
      (assert-valid! (json/parse-string payload true))
      (catch Exception e
        (print (str "\n" (tag :red "Invalid Payload!") " " (.getMessage e)))))))

;; 3. Filter inside the consumer.
;;    Only print incidents where :severity is "critical".
;;    Think about where the right place to filter is — before or after parsing?
(defn critical? [incident]
  (and (some? incident) (= "critical" (:severity incident))))

;; 5. Challenge — count by source.
;;    Use an atom to accumulate a frequency count of incidents by :source.
;;    Print the atom after each poll batch.
;;    Observe it grow as the producer sends more messages.

(def incident-summary (atom {}))

(defn add-incident! [summary incident]
  (when (some? incident)
    (let [source (:source incident)
          curr-count (get @summary source 0)]
      (swap! summary assoc source (inc curr-count)))))


(println "Consumer Started 🚀")
(println "---------------------")
(println "Press CTRL+C to stop!\n")

;; 1. Poll and print raw records.
;; kafka/poll! returns a seq of records for the given timeout (ms).
(with-open [consumer (kafka/subscribed-consumer consumer-config topics-config)]
  (loop []
    (let [records (kafka/poll consumer 5000)] ; 5 seconds
      (print (str "\n" (tag :cyan "Batch Size") " ") (count records))
      (doseq [rec records]

        ;; Print Message
        (print (str "\n" (tag :green "New Message") " "))
        (-> rec (dissoc :value) println)

        ;; Parse the Incident
        (let [incident (parse-incident rec)]
          (add-incident! incident-summary incident)
          (when (critical? incident)
            (println (tag :green "Payload") " " incident))))

      (println (str "\n" (tag :cyan "Batch Summary")))
      (pprint @incident-summary))

    (recur))) ; jump back to loop
