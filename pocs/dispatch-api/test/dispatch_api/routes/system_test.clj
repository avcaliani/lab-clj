(ns dispatch-api.routes.system-test
  (:require [clojure.test :refer [deftest is]]
            [dispatch-api.config :as config]
            [dispatch-api.routes.system :refer [routes]]
            [ring.mock.request :refer [request]]))

(deftest version-route-test
  (doseq [environment ["local" "docker"]]
    (with-redefs [config/api-version! (constantly "0.0.1-alpha")
                  config/api-env! (constantly environment)]
      (let [response (->> "/version" (request :get) routes)]
        (is (= 200 (:status response)))
        (is (= {:version "0.0.1-alpha" :environment environment}
               (:body response)))))))
