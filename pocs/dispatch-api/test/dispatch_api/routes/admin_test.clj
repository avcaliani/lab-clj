(ns dispatch-api.routes.admin-test
  (:require [clojure.test :refer [deftest is testing]]
            [dispatch-api.infra.dynamodb :as db]
            [dispatch-api.routes.admin :refer [routes]]
            [dispatch-api.test-helpers :as helpers]
            [ring.mock.request :refer [request]]))

(deftest list-tables-route-test
  (let [test-routes (routes helpers/test-deps)]
    (testing "/admin/db/tables returns what the db client lists"
      (let [received (atom nil)]
        (with-redefs [db/list-tables (fn [client]
                                       (reset! received client)
                                       {:error? false :TableNames ["springfield-incidents"]})]
          (let [response (->> "/admin/db/tables" (request :get) test-routes)]
            (is (= 200 (:status response)))
            (is (= {:TableNames ["springfield-incidents"]} (:body response)))
            (is (= :fake-db-client @received))))))

    (testing "/admin/db/tables returns 503 with the error details when the db fails"
      (with-redefs [db/list-tables (constantly {:error?        true
                                                :error-type    :cognitect.anomalies/unavailable
                                                :error-message "connection refused"})]
        (let [response (->> "/admin/db/tables" (request :get) test-routes)]
          (is (= 503 (:status response)))
          (is (= {:error-type    :cognitect.anomalies/unavailable
                  :error-message "connection refused"}
                 (:body response))))))

    (testing "other methods and paths are not matched"
      (is (nil? (->> "/admin/db/tables" (request :post) test-routes)))
      (is (nil? (->> "/admin/db" (request :get) test-routes))))))
