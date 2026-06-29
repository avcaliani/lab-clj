(ns dispatch-api.core-test
  (:require [clojure.test :refer [deftest is]]
            [dispatch-api.core :refer [app]]
            [ring.mock.request :refer [request]]))

(deftest hello-world-test
  (let [response (->> "/" (request :get) app)]
    (is (= 200 (:status response)))))
