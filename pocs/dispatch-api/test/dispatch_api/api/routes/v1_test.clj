(ns dispatch-api.api.routes.v1-test
  (:require [clojure.test :refer [deftest is testing]]
            [dispatch-api.api.routes.v1 :refer [routes]]
            [dispatch-api.services.incidents :as service]
            [dispatch-api.test-helpers :as helpers]
            [ring.mock.request :refer [request]]))

(deftest get-incidents-route-test
  (let [test-routes (routes helpers/test-deps)]
    (testing "GET /v1/incidents returns what the service lists"
      (let [received  (atom nil)
            incidents [{:id "inc-1" :severity "high"}]]
        (with-redefs [service/all-incidents! (fn [db]
                                               (reset! received db)
                                               incidents)]
          (let [response (->> "/v1/incidents" (request :get) test-routes)]
            (is (= 200 (:status response)))
            (is (= incidents (:body response)))
            (is (= :fake-db-client @received))))))

    (testing "GET /v1/incidents lets failures propagate to wrap-exceptions"
      (with-redefs [service/all-incidents! (fn [_] (throw (ex-info "app <-> db error" {:status 503})))]
        (is (thrown-with-msg? clojure.lang.ExceptionInfo #"app <-> db error"
                              (->> "/v1/incidents" (request :get) test-routes)))))

    (testing "POST /v1/incidents is still a stub"
      (let [response (->> "/v1/incidents" (request :post) test-routes)]
        (is (= 201 (:status response)))))

    (testing "other methods and paths are not matched"
      (is (nil? (->> "/v1/incidents" (request :delete) test-routes)))
      (is (nil? (->> "/v1/unknown" (request :get) test-routes))))))
