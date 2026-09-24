(ns dispatch-api.middleware-test
  (:require [clojure.test :refer [deftest is testing]]
            [dispatch-api.config :as config]
            [dispatch-api.middleware :refer [wrap-exceptions]]
            [ring.mock.request :refer [request]]))

(def ok-response {:status 200 :body {:ok true}})

(defn- throwing-handler [ex]
  (fn [_] (throw ex)))

(deftest wrap-exceptions-test
  (testing "passes the response through when nothing is thrown"
    (let [handler (wrap-exceptions (constantly ok-response))]
      (is (= ok-response (handler (request :get "/"))))))

  (testing "uses status and message from ex-info"
    (let [handler (wrap-exceptions (throwing-handler (ex-info "Invalid Payload" {:status 422})))
          response (handler (request :get "/"))]
      (is (= 422 (:status response)))
      (is (= {:error "Invalid Payload"} (:body response)))))

  (testing "defaults to 400 when ex-info has no status"
    (let [handler (wrap-exceptions (throwing-handler (ex-info "Bad Request" {})))]
      (is (= 400 (:status (handler (request :get "/")))))))

  (testing "returns the server error response for unplanned exceptions"
    (let [handler (wrap-exceptions (throwing-handler (ArithmeticException. "Divide by zero")))]
      (is (= (:server-error config/error-responses)
             (handler (request :get "/")))))))
