;;; Exercise 02 — Local API server
;;; Run this first: clj ex02_server.clj
;;; Server starts on http://localhost:5000
;;;

(ns ex02-server
  (:require [org.httpkit.server :refer [run-server]]
            [cheshire.core :as json]))

(def incidents
  [{:id "1" :reporter "Homer Simpson"    :source "springfield-nuclear" :severity "critical" :description "Donut stuck in reactor panel"}
   {:id "2" :reporter "Bart Simpson"     :source "kwik-e-mart"         :severity "low"      :description "Squishee machine overflowing"}
   {:id "3" :reporter "Marge Simpson"    :source "springfield-nuclear" :severity "high"     :description "Radiation alarm ignored again"}
   {:id "4" :reporter "Apu Nahasapeemapetilon" :source "kwik-e-mart"   :severity "medium"   :description "Hot dog expired since 1997"}
   {:id "5" :reporter "Homer Simpson"    :source "springfield-nuclear" :severity "critical" :description "Fell asleep on the control panel"}
   {:id "6" :reporter "Ned Flanders"     :source "kwik-e-mart"         :severity "low"      :description "Price tag says 6 but charged 7"}])

(defn maybe-fail [response]
  ;; 20% chance of a 500 — Homer fell asleep again.
  (if (< (rand) 0.2)
    {:status  500
     :headers {"Content-Type" "application/json"}
     :body    (json/generate-string {:message "D'oh! Something went wrong."})}
    response))

(defn app [req]
  (let [method (:request-method req)
        uri    (:uri req)
        body   (:body req)]
    (cond
      (and (= method :get) (= uri "/incidents"))
      (maybe-fail {:status  200
                   :headers {"Content-Type" "application/json"}
                   :body    (json/generate-string incidents)})

      (and (= method :post) (= uri "/incidents"))
      (maybe-fail {:status  201
                   :headers {"Content-Type" "application/json"}
                   :body    (json/generate-string (-> body
                                                      slurp
                                                      (json/parse-string true)
                                                      (assoc :id "7")))})

      :else
      {:status  404
       :headers {"Content-Type" "application/json"}
       :body    (json/generate-string {:message "not found"
                                       :method method
                                       :path uri})})))

;; -main is the conventional entry point
;; tooling looks for this name, like Python's __main__
(defn -main []
  (run-server app {:port 8080})
  (println (str "\033[1;32mIncidents API 🚨\033[0m\n"
               "Server running on http://localhost:8080\n"
               "Press \033[1mCtrl+C\033[0m to stop.\n\n"
               "Available Routes 👇\n"
               " GET  /incidents  — Retrieve all incidents\n"
               " POST /incidents  — Create an incident")))

(-main)
