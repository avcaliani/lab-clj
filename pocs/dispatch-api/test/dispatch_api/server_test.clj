(ns dispatch-api.server-test
  (:require [clojure.test :refer [deftest is testing]]
            [dispatch-api.server :refer [handler]]
            [ring.mock.request :refer [request]]))

(deftest routes-test
  (testing "/api/version returns 200"
    (let [response (handler (request :get "/api/version"))]
      (is (= 200 (:status response)))))

  (testing "unknown paths return a JSON 404"
    (doseq [uri ["/api" "/" "/homer/donuts"]]
      (let [response (handler (request :get uri))]
        (is (= 404 (:status response)) uri)))))

(deftest cors-test
  (testing "responses carry the allow-origin header when an Origin is sent"
    (let [response (handler (-> (request :get "/api/version")
                                (assoc-in [:headers "origin"] "http://localhost:3000")))]
      (is (= "http://localhost:3000"
             (get-in response [:headers "Access-Control-Allow-Origin"])))))

  (testing "preflight OPTIONS is answered before routing"
    (let [response (handler (-> (request :options "/api/v1/incidents")
                                (assoc-in [:headers "origin"] "http://localhost:3000")
                                (assoc-in [:headers "access-control-request-method"] "POST")
                                (assoc-in [:headers "access-control-request-headers"] "content-type")))]
      (is (= 200 (:status response)))
      (is (re-find #"POST" (get-in response [:headers "Access-Control-Allow-Methods"] ""))))))
