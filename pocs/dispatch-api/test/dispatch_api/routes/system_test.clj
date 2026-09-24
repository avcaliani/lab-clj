(ns dispatch-api.routes.system-test
  (:require [clojure.test :refer [deftest is]]
            [dispatch-api.routes.system :refer [routes]]
            [ring.mock.request :refer [request]]))

(deftest version-route-test
  (let [response (->> "/version" (request :get) routes)]
    (is (= 200 (:status response)))))
