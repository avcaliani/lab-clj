(ns dispatch-api.api.routes.v1
  (:require [compojure.core :refer [context GET POST]]
            [dispatch-api.services.incidents :as service]))

(defn routes [{:keys [db]}]
  (context "/v1" []
    (GET "/incidents" [] {:status 200 :body (service/all-incidents! db)})
    (POST "/incidents" [] {:status 201 :body "TODO"})))
