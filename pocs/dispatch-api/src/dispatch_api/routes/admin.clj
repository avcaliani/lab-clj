(ns dispatch-api.routes.admin
  (:require [compojure.core :refer [context GET]]
            [dispatch-api.infra.dynamodb :as db]))

(defn routes [{:keys [db]}]
  (context "/admin" []
    (GET "/db/tables" [] {:status 200
                          :body (db/list-tables db)})))
