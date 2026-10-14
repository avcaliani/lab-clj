(ns dispatch-api.routes.system-test
  (:require [clojure.test :refer [deftest is]]
            [dispatch-api.config-test :as config-test]
            [dispatch-api.routes.system :refer [routes]]
            [ring.mock.request :refer [request]]))

(deftest version-route-test
  (let [routes-mock (routes config-test/env-variables-mock)
        response (->> "/version" (request :get) routes-mock)]
    (is (= 200 (:status response)))
    (is (= {:version "0.0.0" :environment "unit-test"}
           (:body response)))))
