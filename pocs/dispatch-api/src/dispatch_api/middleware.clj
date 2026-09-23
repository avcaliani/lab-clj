(ns dispatch-api.middleware)

;; Why a anonymous function?
;; Because a middleware is a function that returns a new handler,
;; and a handler has to be a function.
(defn log-request! [handler]
  (fn [request]
    (println (str "[" (java.time.Instant/now) "]")
             (:request-method request)
             (:uri request))
    (handler request)))
