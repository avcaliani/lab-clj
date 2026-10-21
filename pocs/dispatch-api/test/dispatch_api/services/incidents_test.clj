(ns dispatch-api.services.incidents-test
  (:require [clojure.test :refer [deftest is testing]]
            [dispatch-api.infra.dynamodb :as db]
            [dispatch-api.services.incidents :as service]))

;; What do we test here?
;;   Only the logic we own: scanning the right table and mapping DynamoDB items
;;   (`{:attr {:S "value"}}`) into flat DTOs with kebab-case keys.
(def ^:private db-item
  {:id          {:S "inc-1"}
   :severity    {:S "high"}
   :status      {:S "closed"}
   :reporter    {:S "ned"}
   :source      {:S "phone"}
   :description {:S "Donut shortage"}
   :reported_at {:S "2026-01-01T10:00:00Z"}
   :ended_at    {:S "2026-01-01T12:00:00Z"}
   :end_reason  {:S "resolved"}})

(def ^:private dto
  {:id          "inc-1"
   :severity    "high"
   :status      "closed"
   :reporter    "ned"
   :source      "phone"
   :description "Donut shortage"
   :reported-at "2026-01-01T10:00:00Z"
   :ended-at    "2026-01-01T12:00:00Z"
   :end-reason  "resolved"})

(deftest all-incidents-test
  (testing "scans the incidents table of the given db client"
    (let [received (atom nil)]
      (with-redefs [db/full-scan! (fn [client table]
                                    (reset! received [client table])
                                    [])]
        (doall (service/all-incidents :fake-db-client))
        (is (= [:fake-db-client "springfield-incidents"] @received)))))

  (testing "maps db items to DTOs"
    (with-redefs [db/full-scan! (constantly [db-item])]
      (is (= [dto] (service/all-incidents :fake-db-client)))))

  (testing "keeps one DTO per item, in order"
    (with-redefs [db/full-scan! (constantly [db-item (assoc db-item :id {:S "inc-2"})])]
      (is (= ["inc-1" "inc-2"] (map :id (service/all-incidents :fake-db-client))))))

  (testing "attributes missing from the item become nil"
    (with-redefs [db/full-scan! (constantly [(dissoc db-item :ended_at :end_reason)])]
      (let [[incident] (service/all-incidents :fake-db-client)]
        (is (= "inc-1" (:id incident)))
        (is (nil? (:ended-at incident)))
        (is (nil? (:end-reason incident))))))

  (testing "no items yields no incidents"
    (with-redefs [db/full-scan! (constantly [])]
      (is (empty? (service/all-incidents :fake-db-client))))
    (with-redefs [db/full-scan! (constantly nil)]
      (is (empty? (service/all-incidents :fake-db-client)))))

  (testing "db failures propagate to wrap-exceptions"
    (with-redefs [db/full-scan! (fn [_ _] (throw (ex-info "app <-> db error" {:status 503})))]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo #"app <-> db error"
                            (service/all-incidents :fake-db-client))))))
