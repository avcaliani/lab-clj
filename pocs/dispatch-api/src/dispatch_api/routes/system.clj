(ns dispatch-api.routes.system
  (:require [compojure.core :refer [GET]]))

(defn routes [env]
  (GET "/version" [] {:status 200
                      :body {:version (:api-version env)
                             :environment (:api-env env)}}))
