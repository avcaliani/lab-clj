(ns dispatch-api.routes.admin
  (:require [compojure.core :refer [context GET]]
            [dispatch-api.infra.dynamodb :as dynamodb]))

;; Admin endpoints have very strict access, NEVER public.
;; Exception: this is a PoC and that's the only reason they are public.
(defn routes
  "Admin routes. Expects `deps` to hold a `:db` client."
  [{:keys [db]}]
  (context "/admin" []
    (GET "/db/tables" [] {:status 200 :body (dynamodb/list-tables db)})))
