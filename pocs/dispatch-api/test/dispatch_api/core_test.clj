(ns dispatch-api.core-test
  (:require [clojure.test :refer [deftest is]]
            [dispatch-api.core :refer [app]]
            [ring.mock.request :refer [request]]))

;; TODO: Deprecate this Test in the Future
(deftest hello-world-test
  (let [response (->> "/api/version" (request :get) app)]
    (is (= 200 (:status response)))))
