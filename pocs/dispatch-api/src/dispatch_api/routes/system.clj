(ns dispatch-api.routes.system
  (:require [compojure.core :refer [defroutes GET]]
            [dispatch-api.config :as config]))

(defroutes routes
  (GET "/version" [] {:status 200 :body {:version (config/api-version!)}}))

