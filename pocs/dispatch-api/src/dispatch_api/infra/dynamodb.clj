(ns dispatch-api.infra.dynamodb
  (:require [cognitect.aws.client.api :as aws]
            [cognitect.aws.credentials :as credentials]))

(defn- local? [env]
  (contains? #{"local" "docker"} env))

(defn- local-overrides
  "Configuration to run only on your local machine.
  Includes, endpoint pointing to localhost and fake aws credentials."
  [host port]
  {:endpoint-override {:protocol :http
                       :hostname host
                       :port port}
   :credentials-provider (credentials/basic-credentials-provider {:access-key-id "local"
                                                                  :secret-access-key "local"})})

(defn start! [config]
  (let [local-run?  (local? (:environment config))
        base-params {:api :dynamodb
                     :region (-> config :db :region)}
        overrides   (when local-run?
                      (local-overrides (-> config :db :host) (-> config :db :port)))]

    (aws/client (merge base-params overrides))))

;; Client Operations
;; https://cognitect-labs.github.io/aws-api/cognitect.aws.client.api-api.html
(defn list-tables [client]
  (aws/invoke client {:op :ListTables}))
