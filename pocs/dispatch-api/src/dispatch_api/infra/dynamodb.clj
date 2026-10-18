(ns dispatch-api.infra.dynamodb
  (:require [cognitect.aws.client.api :as aws]
            [cognitect.aws.credentials :as credentials]))

(defn- local? [env]
  (contains? #{"local" "docker"} env))

(defn- local-overrides
  "Overrides for running on your local machine: an endpoint pointing to `host:port`
   and fake AWS credentials."
  [host port]
  {:endpoint-override {:protocol :http
                       :hostname host
                       :port port}
   :credentials-provider (credentials/basic-credentials-provider {:access-key-id "local"
                                                                  :secret-access-key "local"})})

(defn make-client
  "Builds the DynamoDB client. In `local` and `docker` it points to the local
   endpoint with fake credentials, in any other environment it uses the default AWS ones."
  [config]
  (let [local-run?  (local? (:environment config))
        base-params {:api :dynamodb
                     :region (-> config :db :region)}
        overrides   (when local-run?
                      (local-overrides (-> config :db :host) (-> config :db :port)))]

    (aws/client (merge base-params overrides))))

(defn- check-error!
  "If an error occurs while interacting with DynamoDB, an anomaly field is expected to be present.
   Returns the response or raises an exception that will be caught by the middleware."
  [response]
  (let [error? (contains? response :cognitect.anomalies/category)]
    (when error?
      (throw (ex-info "app <-> db error" {:status        503
                                          :error-type    (:cognitect.anomalies/category response)
                                          :error-message (:cognitect.anomalies/message response)})))
    response))

;; Client Operations
;; https://cognitect-labs.github.io/aws-api/cognitect.aws.client.api-api.html
(defn list-tables
  "Lists the DynamoDB tables as `{:tables [...]}`.
   Throws a 503 ex-info on AWS anomalies."
  [client]
  (let [response (check-error! (aws/invoke client {:op :ListTables}))]
    {:tables (:TableNames response)}))
