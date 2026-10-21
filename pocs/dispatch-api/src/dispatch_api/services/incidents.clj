(ns dispatch-api.services.incidents
  (:require [dispatch-api.infra.dynamodb :as dynamodb]))

(def ^:private table-name "springfield-incidents")

;; This could be in an adapter component/layer, won't do for simplicity.
(defn- db-model->dto [incident]
  {:id          (-> incident :id :S)
   :severity    (-> incident :severity :S)
   :status      (-> incident :status :S)
   :reporter    (-> incident :reporter :S)
   :source      (-> incident :source :S)
   :description (-> incident :description :S)
   :reported-at (-> incident :reported_at :S)
   :ended-at    (-> incident :ended_at :S)
   :end-reason  (-> incident :end_reason :S)})

(defn all-incidents! [db]
  (let [results (dynamodb/full-scan! db table-name)]
    (mapv db-model->dto results)))
