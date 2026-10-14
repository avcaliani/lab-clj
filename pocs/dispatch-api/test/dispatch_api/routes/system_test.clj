(ns dispatch-api.routes.system-test
  (:require [clojure.test :refer [deftest is]]
            [dispatch-api.routes.system :refer [routes]]
            [dispatch-api.test-helpers :as helpers]
            [ring.mock.request :refer [request]]))

(deftest version-route-test
  (let [test-routes (routes helpers/test-deps)
        response (->> "/version" (request :get) test-routes)]
    (is (= 200 (:status response)))
    (is (= {:version "0.0.0" :environment "unit-test"}
           (:body response)))))
