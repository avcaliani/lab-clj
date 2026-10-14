(ns dispatch-api.routes.system
  (:require [compojure.core :refer [GET]]))

(defn routes
  "System routes. Expects `deps` to hold a `:config` map."
  [{:keys [config]}]
  (GET "/version" [] {:status 200
                      :body {:version (:version config)
                             :environment (:environment config)}}))
