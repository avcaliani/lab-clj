(ns dispatch-api.api.routes.v1
  (:require [compojure.core :refer [context GET POST]]
            [dispatch-api.services.incidents :as service]))

;; Handlers ------------------------------------------

(defn- get-incidents-handler! [db {:strs [source]}]
  (if source
    (service/find-by-source! db source)
    (service/all-incidents! db)))

;; Endpoints -----------------------------------------

(defn routes [{:keys [db]}]
  (context "/v1" []
    (GET "/incidents" {params :query-params}
      {:status 200 :body (get-incidents-handler! db params)})

    (GET "/incidents/:id" [id]
      {:status 200 :body (service/find-incident! db id)})

    (POST "/incidents" []
      {:status 201 :body "TODO"})))

