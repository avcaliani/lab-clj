(ns dispatch-api.config)

;; Environment Variables --------------------------------------------
(defn load-config!
  "Reads the app config from environment variables, with defaults."
  []
  {:version     (or (System/getenv "API_VERSION") "0.0.1-alpha")
   :environment (or (System/getenv "API_ENVIRONMENT") "local")
   :db          {:region (or (System/getenv "DB_REGION") "us-east-1")
                 :host   (or (System/getenv "DB_HOSTNAME") "localhost")
                 :port   (or (System/getenv "DB_PORT") 8000)}})

;; Headers ----------------------------------------------------------
(def default-headers {"Content-Type" "application/json; charset=utf-8"})

;; Errors -----------------------------------------------------------
(def error-responses
  {:not-found {:status 404
               :headers default-headers
               :body {:error "Not Found 🫪"}}
   :malformed {:status 400
               :headers default-headers
               :body {:error "Malformed JSON ⚠️"}}
   :server-error {:status 500
                  :headers default-headers
                  :body {:error "Internal Server Error 💥"}}})
