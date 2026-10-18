(ns dispatch-api.infra.dynamodb-test
  (:require [clojure.test :refer [deftest is testing]]
            [cognitect.aws.client.api :as aws]
            [dispatch-api.infra.dynamodb :as db]))

;; What do we test here?
;;   Only the logic we own:
;;     - local overrides (endpoint + fake credentials) apply to local/docker and never to
;;       any other environment.
;;     - aws anomalies (aws/invoke does not throw) are turned into a 503 ex-info.
;;   We don't test that the AWS client talks to DynamoDB, that needs an integration test.

(defn- config [environment]
  {:environment environment
   :db          {:region "sa-east-1" :host "dynamodb-local" :port 8000}})

(defn- client-params
  "Runs `start!` with a stubbed aws/client and returns the params it received."
  [environment]
  (with-redefs [aws/client identity]
    (db/start! (config environment))))

(deftest start-test
  (testing "local and docker override the endpoint and credentials"
    (doseq [env ["local" "docker"]]
      (let [params (client-params env)]
        (is (contains? params :endpoint-override) env)
        (is (contains? params :credentials-provider) env))))

  (testing "any other environment never overrides them"
    (doseq [env ["dev" "prod" "unit-test"]]
      (let [params (client-params env)]
        (is (not (contains? params :endpoint-override)) env)
        (is (not (contains? params :credentials-provider)) env))))

  (testing "builds a real client without touching the network"
    (is (some? (db/start! (config "local"))))))

(defn- list-tables-with
  "Runs `list-tables` with aws/invoke stubbed to return `response`."
  [response]
  (with-redefs [aws/invoke (constantly response)]
    (db/list-tables :fake-client)))

(deftest list-tables-test
  (testing "a successful response is reduced to the table names"
    (is (= {:tables ["springfield-incidents"]}
           (list-tables-with {:TableNames ["springfield-incidents"]}))))

  (testing "a response without table names yields nil tables"
    (is (= {:tables nil} (list-tables-with {}))))

  (testing "it sends a ListTables request to the given client"
    (let [received (atom nil)]
      (with-redefs [aws/invoke (fn [client request]
                                 (reset! received [client request])
                                 {:TableNames []})]
        (db/list-tables :fake-client)
        (is (= [:fake-client {:op :ListTables}] @received)))))

  (testing "an aws anomaly throws a 503 ex-info with its type and message"
    (let [ex (is (thrown? clojure.lang.ExceptionInfo
                          (list-tables-with {:cognitect.anomalies/category :cognitect.anomalies/unavailable
                                             :cognitect.anomalies/message  "connection refused"})))]
      (is (= "app <-> db error" (ex-message ex)))
      (is (= {:status        503
              :error-type    :cognitect.anomalies/unavailable
              :error-message "connection refused"}
             (ex-data ex))))))
