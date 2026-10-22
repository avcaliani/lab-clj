(ns dispatch-api.api.routes.admin-test
  (:require [clojure.test :refer [deftest is testing]]
            [dispatch-api.infra.dynamodb :as db]
            [dispatch-api.api.routes.admin :refer [routes]]
            [dispatch-api.test-helpers :as helpers]
            [ring.mock.request :refer [request]]))

(deftest list-tables-route-test
  (let [test-routes (routes helpers/test-deps)]
    (testing "/admin/db/tables returns what the db client lists"
      (let [received (atom nil)]
        (with-redefs [db/list-tables! (fn [client]
                                        (reset! received client)
                                        {:tables ["springfield-incidents"]})]
          (let [response (->> "/admin/db/tables" (request :get) test-routes)]
            (is (= 200 (:status response)))
            (is (= {:tables ["springfield-incidents"]} (:body response)))
            (is (= :fake-db-client @received))))))

    (testing "/admin/db/tables lets db failures propagate to wrap-exceptions"
      (with-redefs [db/list-tables! (fn [_] (throw (ex-info "app <-> db error" {:status 503})))]
        (is (thrown-with-msg? clojure.lang.ExceptionInfo #"app <-> db error"
                              (->> "/admin/db/tables" (request :get) test-routes)))))

    (testing "other methods and paths are not matched"
      (is (nil? (->> "/admin/db/tables" (request :post) test-routes)))
      (is (nil? (->> "/admin/db" (request :get) test-routes))))))
