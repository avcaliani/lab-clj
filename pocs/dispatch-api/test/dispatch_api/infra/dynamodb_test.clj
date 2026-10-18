(ns dispatch-api.infra.dynamodb-test
  (:require [clojure.test :refer [deftest is testing]]
            [cognitect.aws.client.api :as aws]
            [cognitect.aws.credentials :as credentials]
            [dispatch-api.infra.dynamodb :as db]))

(defn- config [environment]
  {:environment environment
   :db          {:region "sa-east-1" :host "dynamodb-local" :port 8000}})

(defn- client-params
  "Runs `start!` with a stubbed aws/client and returns the params it received."
  [environment]
  (with-redefs [aws/client identity]
    (db/start! (config environment))))

(deftest start-test
  (testing "local and docker point to the local endpoint with fake credentials"
    (doseq [env ["local" "docker"]]
      (let [params (client-params env)]
        (is (= :dynamodb (:api params)) env)
        (is (= "sa-east-1" (:region params)) env)
        (is (= {:protocol :http :hostname "dynamodb-local" :port 8000}
               (:endpoint-override params))
            env)
        (is (= "local" (-> params :credentials-provider credentials/fetch :aws/access-key-id))
            env))))

  (testing "any other environment uses the default endpoint and credentials chain"
    (doseq [env ["dev" "prod" "unit-test"]]
      (let [params (client-params env)]
        (is (= {:api :dynamodb :region "sa-east-1"} params) env))))

  (testing "builds a real client without touching the network"
    (is (some? (db/start! (config "local"))))))

(deftest list-tables-test
  (testing "invokes the ListTables op on the given client"
    (let [calls (atom [])]
      (with-redefs [aws/invoke (fn [client request]
                                 (swap! calls conj [client request])
                                 {:TableNames ["springfield-incidents"]})]
        (is (= {:TableNames ["springfield-incidents"]}
               (db/list-tables :fake-client)))
        (is (= [[:fake-client {:op :ListTables}]] @calls))))))
