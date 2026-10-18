(ns dispatch-api.routes.admin
  (:require [compojure.core :refer [context GET]]
            [dispatch-api.infra.dynamodb :as db]))

;; Admin endpoints have very strict access, NEVER public.
;; Exception: this is a PoC and that's the only reason they are public.
(defn routes [{:keys [db]}]
  (context "/admin" []
    (GET "/db/tables" [] (let [result (db/list-tables db)
                               status-code (if (:error? result) 503 200)
                               payload (dissoc result :error?)]
                           {:status status-code :body payload}))))
