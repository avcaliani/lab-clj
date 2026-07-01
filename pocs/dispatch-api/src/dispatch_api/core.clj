(ns dispatch-api.core
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defroutes app
  (GET "/" [] {:status 200
               :body "Springfield Emergency Dispatch"}))

(defn -main
  "Starts the Jetty server.
  Accepts an optional port as the first CLI arg (default: 8080).
  Usage: lein run [port]"
  [& args]
  (let [port (Integer/parseInt (or (first args) "8080"))]
    (run-jetty app {:port 8080 :join? false})
    (println (str "---------------\n"
                  "Dispatch API ⚡️\n"
                  "---------------\n"
                  "Server running on http://localhost:" port "\n"
                  "Press Ctrl+C to stop.\n"))))
