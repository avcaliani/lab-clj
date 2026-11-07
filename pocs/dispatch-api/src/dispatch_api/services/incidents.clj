(ns dispatch-api.services.incidents
  (:require [dispatch-api.infra.dynamodb :as dynamodb]))

(def ^:private table-name "springfield-incidents")
(def ^:private table-indexes {:source {:index-name "source-index"
                                       :index-key  "source"}})

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

(defn find-incident! [db incident-id]
  (let [result (dynamodb/find-one! db table-name incident-id)]
    (if result
      (db-model->dto result)
      (throw (ex-info "incident not found" {:status 404
                                            :incident-id incident-id})))))

(defn find-by-source! [db source-name]
  (let [tbl-index (conj (:source table-indexes) {:value source-name})
        results (dynamodb/find-by! db table-name tbl-index)]
    (mapv db-model->dto results)))

;; Never executed in Production, but useful for testing in the REPL.
;; Usually I'd not leave it here, but I'm leaving as an example.
(comment
  (def client (dynamodb/make-client {:environment "local"
                                     :db {:host "localhost"
                                          :port 8000
                                          :region "us-east-1"}}))

  (find-incident! client "7FF64DC1-420C-49BB-B48B-C64FD73E13D8")
  (find-incident! client "i-dont-exist")
  (find-by-source! client "springfield-nuclear")

  (let [tbl-index (conj (:source table-indexes) {:value "springfield-nuclear"})]
    (dynamodb/find-by! client table-name tbl-index))
  )
