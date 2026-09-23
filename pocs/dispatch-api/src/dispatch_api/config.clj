(ns dispatch-api.config)

(defn api-version! [] (or (System/getenv "API_VERSION") "local-execution"))
