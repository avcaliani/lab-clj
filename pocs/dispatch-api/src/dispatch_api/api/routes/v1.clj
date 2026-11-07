(ns dispatch-api.api.routes.v1
  (:require [clojure.string :as str]
            [compojure.core :refer [context GET POST]]
            [dispatch-api.services.incidents :as service]))

;; Handlers ------------------------------------------

(defn- get-incidents-handler! [db {:strs [source]}]
  (if (str/blank? source)
    (service/all-incidents! db)
    (service/find-by-source! db source)))

;; Endpoints -----------------------------------------

(defn routes [{:keys [db]}]
  (context "/v1" []
    (GET "/incidents" {params :query-params}
      {:status 200 :body (get-incidents-handler! db params)})

    (GET "/incidents/:id" [id]
      {:status 200 :body (service/find-incident! db id)})

    (POST "/incidents" []
      {:status 201 :body "TODO"})))
