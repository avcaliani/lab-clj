;;; Exercise 02
;;;     Local API server
;;;     Server starts on http://localhost:8080
(ns ex02-server
  (:require [cheshire.core :as json]
            [org.httpkit.server :refer [run-server]]
            [colors :refer [colorize]]))

(def incidents
  [{:id "1" :reporter "Homer Simpson"    :source "springfield-nuclear" :severity "critical" :description "Donut stuck in reactor panel"}
   {:id "2" :reporter "Bart Simpson"     :source "kwik-e-mart"         :severity "low"      :description "Squishee machine overflowing"}
   {:id "3" :reporter "Marge Simpson"    :source "springfield-nuclear" :severity "high"     :description "Radiation alarm ignored again"}
   {:id "4" :reporter "Apu Nahasapeemapetilon" :source "kwik-e-mart"   :severity "medium"   :description "Hot dog expired since 1997"}
   {:id "5" :reporter "Homer Simpson"    :source "springfield-nuclear" :severity "critical" :description "Fell asleep on the control panel"}
   {:id "6" :reporter "Ned Flanders"     :source "kwik-e-mart"         :severity "low"      :description "Price tag says 6 but charged 7"}])

(defn- new-incident [payload]
  (-> payload
      slurp
      (json/parse-string true)
      (assoc :id "7")))

(defn- envelope
  [{:keys [status body]}]
  {:status  status
   :headers {"Content-Type" "application/json"}
   :body    (json/generate-string body)})

(defn- maybe-fail
  "20% chance of a 500 — Homer fell asleep again.
   Also adds a random delay in the request, min 500ms up to 2.5s"
  [response]
  (let [random-delay (+ 500 (rand-int 2000))]
    (Thread/sleep random-delay)
    (if (< (rand) 0.2)
      (envelope {:status  500
                 :body    {:message "D'oh! Something went wrong."}})
      response)))

(defn app [req]
  (let [method (:request-method req)
        uri    (:uri req)
        body   (:body req)]

    (cond
      (and (= method :get) (= uri "/incidents"))
      (maybe-fail (envelope {:status  200
                             :body    incidents}))

      (and (= method :post) (= uri "/incidents"))
      (maybe-fail (envelope {:status  201
                             :body  (new-incident body)}))

      :else (envelope {:status  404
                       :body    {:message "not found"
                                 :method method
                                 :path uri}}))))

;; -main is the conventional entry point
;; tooling looks for this name, like Python's __main__
(defn -main []
  (run-server app {:port 8080})
  (println (str (colorize :green "Incidents API 🚨") "\n"
                "Server running on http://localhost:8080\n"
                "Press " (colorize :bold "Ctrl+C") " to stop.\n\n"
                "Available Routes 👇\n"
                " GET  /incidents  — Retrieve all incidents\n"
                " POST /incidents  — Create an incident")))

(-main)
