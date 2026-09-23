(ns dispatch-api.routes.v1
  (:require [compojure.core :refer [context defroutes GET POST]]))

(defroutes routes
  (context "/v1" []
    (GET "/incidents" [] {:status 200 :body "TODO"})
    (POST "/incidents" [] {:status 201 :body "TODO"})))
