(ns dispatch-api.infra.dynamodb-test
  (:require [clojure.test :refer [deftest is testing]]
            [cognitect.aws.client.api :as aws]
            [dispatch-api.infra.dynamodb :as db]))

;; What do we test here?
;;   Only the logic we own: local overrides (endpoint + fake credentials) apply to
;;   local/docker and never to any other environment.
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
