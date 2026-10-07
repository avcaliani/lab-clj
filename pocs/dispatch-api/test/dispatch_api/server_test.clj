(ns dispatch-api.server-test
  (:require [clojure.test :refer [deftest is testing]]
            [dispatch-api.server :refer [handler]]
            [ring.mock.request :refer [content-type request]]))

(deftest routes-test
  (testing "/api/version returns 200"
    (let [response (handler (request :get "/api/version"))]
      (is (= 200 (:status response)))))

  (testing "unknown paths return a JSON 404"
    (doseq [uri ["/api" "/" "/homer/donuts"]]
      (let [response (handler (request :get uri))]
        (is (= 404 (:status response)) uri)))))

(def ^:private origin "http://localhost:3000")

(defn- with-origin [req]
  (assoc-in req [:headers "origin"] origin))

(deftest cors-test
  (testing "responses carry the allow-origin header when an Origin is sent"
    (let [response (handler (with-origin (request :get "/api/version")))]
      (is (= origin (get-in response [:headers "Access-Control-Allow-Origin"])))))

  (testing "preflight OPTIONS is answered before routing"
    (let [response (handler (-> (request :options "/api/v1/incidents")
                                with-origin
                                (assoc-in [:headers "access-control-request-method"] "POST")
                                (assoc-in [:headers "access-control-request-headers"] "content-type")))]
      (is (= 200 (:status response)))
      (is (re-find #"POST" (get-in response [:headers "Access-Control-Allow-Methods"] "")))
      (is (re-find #"(?i)content-type" (get-in response [:headers "Access-Control-Allow-Headers"] "")))))

  (testing "error responses still carry the allow-origin header"
    (doseq [[status req] [[404 (request :get "/homer/donuts")]
                          [400 (-> (request :post "/api/v1/incidents" "{bad json")
                                   (content-type "application/json"))]]]
      (let [response (handler (with-origin req))]
        (is (= status (:status response)))
        (is (= origin (get-in response [:headers "Access-Control-Allow-Origin"])) (str status)))))

  (testing "requests without an Origin header get no CORS headers"
    (let [response (handler (request :get "/api/version"))]
      (is (= 200 (:status response)))
      (is (nil? (get-in response [:headers "Access-Control-Allow-Origin"])))))

  (testing "preflight for a method outside the allow-list is not granted"
    (let [response (handler (-> (request :options "/api/v1/incidents")
                                with-origin
                                (assoc-in [:headers "access-control-request-method"] "PUT")))]
      (is (nil? (get-in response [:headers "Access-Control-Allow-Origin"]))))))
