(defproject dispatch-api "0.0.1-alpha"
  :description "Homer keeps causing incidents at the nuclear plant.
                This API ingests and queries incident reports from Springfield sources."
  :url "https://github.com/avcaliani/lab-clj"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.12.5"]                 ; https://clojure.org/releases/downloads
                 [ring/ring-core "1.15.5"]                      ; https://clojars.org/ring/ring-core
                 [ring/ring-jetty-adapter "1.15.5"]             ; https://clojars.org/ring/ring-jetty-adapter
                 [compojure "1.7.2"]                            ; https://clojars.org/compojure
                 [com.cognitect.aws/api "0.8.824"]              ; https://github.com/cognitect-labs/aws-api
                 [com.cognitect.aws/endpoints "871.2.46.17"]    ; latest-releases.edn in the repo above
                 [com.cognitect.aws/dynamodb "871.2.42.19"]]    ; latest-releases.edn in the repo above
  :main ^:skip-aot dispatch-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:dependencies [[ring/ring-mock "0.6.2"]]}})  ; https://clojars.org/ring/ring-mock}
