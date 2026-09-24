(ns dispatch-api.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [dispatch-api.core :refer [handler]]
            [ring.mock.request :refer [request]]))

(deftest routes-test
  (testing "/api/version returns 200"
    (let [response (handler (request :get "/api/version"))]
      (is (= 200 (:status response)))))

  (testing "unknown paths return a JSON 404"
    (doseq [uri ["/api" "/" "/homer/donuts"]]
      (let [response (handler (request :get uri))]
        (is (= 404 (:status response)) uri)))))
