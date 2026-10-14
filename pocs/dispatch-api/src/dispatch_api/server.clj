(ns dispatch-api.server
  (:require [compojure.core :refer [context routes]]
            [compojure.route :as route]
            [dispatch-api.config :as config]
            [dispatch-api.middleware :as middleware]
            [dispatch-api.routes.system :as system]
            [dispatch-api.routes.v1 :as v1]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]])
  (:gen-class))

;; Routes -----------------------------------
(defn- app-routes [env]
  (routes
   (context "/api" []
     (system/routes env)
     v1/routes)
   (route/not-found (:not-found config/error-responses))))

;; Middlewares ------------------------------
(defn handler [env]
  (-> (app-routes env)
      (wrap-json-body {:keywords? true
                       :malformed-response (:malformed config/error-responses)})
      middleware/wrap-exceptions
      middleware/wrap-log-request
      wrap-json-response
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :post :options])))

;; Main ------------------------------
(defn -main
  "Starts the Jetty server.
  Accepts an optional port as the first CLI arg (default: 8080).
  Usage: lein run [port]"
  [& args]
  (let [port (Integer/parseInt (or (first args) "8080"))
        env (config/load-env!)]
    ;; The idea is, load vars and start components here ☝️
    ;; Then, inject them in the handlers 👇 to reach where it is necessary
    (run-jetty (handler env) {:port port :join? false})

    ;; Just a Cool Print \o/
    (println (str "\n"
                  "▄▄▄▄  ▄▄  ▄▄▄▄ ▄▄▄▄   ▄▄▄ ▄▄▄▄▄▄ ▄▄▄▄ ▄▄ ▄▄    ▄▄▄  ▄▄▄▄  ▄▄\n"
                  "██▀██ ██ ███▄▄ ██▄█▀ ██▀██  ██  ██▀▀▀ ██▄██   ██▀██ ██▄█▀ ██\n"
                  "████▀ ██ ▄▄██▀ ██    ██▀██  ██  ▀████ ██ ██   ██▀██ ██    ██\n"
                  "\n"
                  "Server running on http://localhost:" port "\n"
                  "Press Ctrl+C to stop.\n"))))
