(ns dispatch-api.server
  (:require [compojure.core :refer [context routes]]
            [compojure.route :as route]
            [dispatch-api.config :as config]
            [dispatch-api.infra.dynamodb :as dynamodb]
            [dispatch-api.middleware :as middleware]
            [dispatch-api.routes.admin :as admin]
            [dispatch-api.routes.system :as system]
            [dispatch-api.routes.v1 :as v1]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]])
  (:gen-class))

;; Routes -----------------------------------
;; Why a `let`?
;;   `context` bodies run on every request, so build deps-based routes once, outside it.
(defn- app-routes [deps]
  (let [system-routes (system/routes deps)
        admin-routes  (admin/routes deps)]
    (routes
     (context "/api" []
       system-routes
       admin-routes
       v1/routes)
     (route/not-found (:not-found config/error-responses)))))

;; Middlewares ------------------------------
(defn handler [deps]
  (-> (app-routes deps)
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
  (let [port   (Integer/parseInt (or (first args) "8080"))
        cfg    (config/load-config!)
        deps   {:config cfg
                :db     (dynamodb/make-client cfg)}]
    ;; The idea is, load config and start components here ☝️
    ;; Then, inject them as `deps` in the handlers 👇 to reach where it is necessary
    (run-jetty (handler deps) {:port port :join? false})

    ;; Just a Cool Print \o/
    (println (str "\n"
                  "▄▄▄▄  ▄▄  ▄▄▄▄ ▄▄▄▄   ▄▄▄ ▄▄▄▄▄▄ ▄▄▄▄ ▄▄ ▄▄    ▄▄▄  ▄▄▄▄  ▄▄\n"
                  "██▀██ ██ ███▄▄ ██▄█▀ ██▀██  ██  ██▀▀▀ ██▄██   ██▀██ ██▄█▀ ██\n"
                  "████▀ ██ ▄▄██▀ ██    ██▀██  ██  ▀████ ██ ██   ██▀██ ██    ██\n"
                  "\n"
                  "Server running on http://localhost:" port "\n"
                  "Press Ctrl+C to stop.\n"))))
