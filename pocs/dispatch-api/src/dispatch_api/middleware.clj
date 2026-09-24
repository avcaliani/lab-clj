(ns dispatch-api.middleware
  (:require [dispatch-api.config :as config])
  (:import clojure.lang.ExceptionInfo))

;; Why an anonymous function?
;;   Because a middleware is a function that returns a new handler,
;;   and a handler has to be a function.
;;
;; Which attributes does request have?
;;   https://github.com/ring-clojure/ring/blob/master/SPEC.md
(defn wrap-log-request [handler]
  (fn [request]
    (let [response (handler request)]
      (println (str "[" (java.time.Instant/now) "]")
               (:request-method request)
               (:uri request)
               (str "[" (:status response) "]"))
      response)))

(defn wrap-exceptions [handler]
  (fn [request]
    (try (handler request)
         ;; Custom Exceptions, will be caught here
         ;; e.g. (throw (ex-info "Invalid Payload" {:status 422}))
         (catch ExceptionInfo e
           {:status (:status (ex-data e) 400)
            :headers config/default-headers
            :body {:error (ex-message e)}})
         ;; Any other exception not planned is caught here
         ;; e.g. division by 0
         (catch Exception e
           (println "Unhandled error:" (ex-message e))
           (:server-error config/error-responses)))))
