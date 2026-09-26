(defproject dispatch-api "0.0.1-alpha"
  :description "Homer keeps causing incidents at the nuclear plant.
                This API ingests and queries incident reports from Springfield sources."
  :url "https://github.com/avcaliani/lab-clj"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  ;; Not used yet, worth keeping on the radar:
  ;;   - logging: https://github.com/taoensso/timbre
  :dependencies [[org.clojure/clojure "1.12.5"]                 ; https://clojure.org/releases/downloads
                 ;; HTTP server + routing
                 [ring/ring-core "1.15.5"]                      ; https://clojars.org/ring/ring-core
                 [ring/ring-jetty-adapter "1.15.5"]             ; https://clojars.org/ring/ring-jetty-adapter
                 [ring/ring-json "0.5.1"]                       ; https://clojars.org/ring/ring-json
                 [compojure "1.7.2"]                            ; https://clojars.org/compojure
                 ;; DynamoDB
                 [com.cognitect.aws/api "0.8.824"]              ; https://github.com/cognitect-labs/aws-api
                 [com.cognitect.aws/endpoints "871.2.46.17"]    ; latest-releases.edn in the repo above
                 [com.cognitect.aws/dynamodb "871.2.42.19"]]    ; latest-releases.edn in the repo above
  :plugins [[lein-cljfmt "0.9.2"]                               ; https://clojars.org/lein-cljfmt
            [com.github.clj-kondo/lein-clj-kondo "2026.05.25"]] ; https://clojars.org/com.github.clj-kondo/lein-clj-kondo
  :main ^:skip-aot dispatch-api.server
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:dependencies [[ring/ring-mock "0.6.2"]]}})  ; https://clojars.org/ring/ring-mock
