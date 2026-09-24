(ns dispatch-api.core
  (:require [compojure.core :refer [context defroutes]]
            [compojure.route :as route]
            [dispatch-api.config :as config]
            [dispatch-api.middleware :as middleware]
            [dispatch-api.routes.system :as system]
            [dispatch-api.routes.v1 :as v1]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]])
  (:gen-class))

;; Routes -----------------------------------
(defroutes app
  (context "/api" []
    system/routes
    v1/routes)
  (route/not-found (:not-found config/error-responses)))

;; Middlewares ------------------------------
(def handler
  (-> app
      (wrap-json-body {:keywords? true
                       :malformed-response (:malformed config/error-responses)})
      middleware/wrap-exceptions
      middleware/wrap-log-request
      wrap-json-response))

;; Main ------------------------------
(defn -main
  "Starts the Jetty server.
  Accepts an optional port as the first CLI arg (default: 8080).
  Usage: lein run [port]"
  [& args]
  (let [port (Integer/parseInt (or (first args) "8080"))]
    (run-jetty handler {:port port :join? false})
    (println (str "---------------\n"
                  "Dispatch API ⚡️\n"
                  "---------------\n"
                  "Server running on http://localhost:" port "\n"
                  "Press Ctrl+C to stop.\n"))))
