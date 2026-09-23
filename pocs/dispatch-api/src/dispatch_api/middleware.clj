(ns dispatch-api.middleware)

;; Why a anonymous function?
;;   Because a middleware is a function that returns a new handler,
;;   and a handler has to be a function.
;;
;; Which attributes does request have?
;;   https://github.com/ring-clojure/ring/blob/master/SPEC.md
(defn log-request! [handler]
  (fn [request]
    (let [response (handler request)]
      (println (str "[" (java.time.Instant/now) "]")
               (:request-method request)
               (:uri request)
               (str "[" (:status response) "]"))
      response)))
