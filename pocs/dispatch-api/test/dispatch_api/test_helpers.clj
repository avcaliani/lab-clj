(ns dispatch-api.test-helpers)

;; What goes here?
;;   Fixtures shared by 2+ test namespaces, like fake `deps` to build routes and handlers.
;;   Keep it dumb: plain data and tiny builders, no assertions and no real clients.
;;   Anything used by a single test file stays private in that file.
(def test-deps
  {:config {:version     "0.0.0"
            :environment "unit-test"}
   :db     :fake-db-client})
