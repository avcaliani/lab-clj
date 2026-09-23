(ns dispatch-api.routes.system
  (:require [compojure.core :refer [defroutes GET]]))

(defroutes routes
  (GET "/version" [] {:status 200 :body {:version "v0"}})) ; TODO: Get from Env

