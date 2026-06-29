(ns dispatch-api.core
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defroutes app
  (GET "/" [] {:status 200
               :body "Springfield Emergency Dispatch"}))

(defn -main [& args]
  (run-jetty app {:port 8080 :join? false})
  (println (str "---------------\n"
                "Dispatch API ⚡️\n"
                "---------------\n"
                "Server running on http://localhost:8080\n"
                "Press Ctrl+C to stop.\n")))
