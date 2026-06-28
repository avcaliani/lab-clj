;;; Exercise 02 — HTTP basics
;;; Start the server first: clj ex02_server.clj
;;; Then run this: clj ex02_http.clj
;;;
;;; New concepts: http-kit client, cheshire (JSON parsing), namespaces + require.

(ns ex02-http
  (:require [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [org.httpkit.client :as http]))

;; require is Clojure's import. :as aliases the namespace — like Scala's import x.y.z.{Foo => F}.
;; Keywords after require (:as, :refer) are options, not data.

(defn- base-url [path] (str "http://localhost:8080" path))

;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Fetch all incidents.
;;    Use (http/get url) to call GET /incidents.
;;    The call returns a promise — deref it with @ to get the response map.
;;    Hint: @(http/get url) gives you {:status 200 :body "...json string..."}

(def response @(http/get (base-url "/incidents")))

(println "API Response 🌐")
(println "---------------")
(pprint response)

;; 1.1. Handle failures.
;;    The server randomly returns 500. Check (:status response) and print a message if it failed.
;;    Hint: use `when` for a one-branch conditional — (when condition expr)

(when (not= 200 (:status response))
  (println "\n\nFailed to retrieve incidents :/")
  (println "Interrupting the script... See ya o/")
  (System/exit 1))

;; 2. Parse the response body.
;;    The body is a JSON string. Use (json/parse-string body true) to get a seq of maps.
;;    The `true` argument keywordizes keys: "source" becomes :source.

(def incidents (-> response
                   :body
                   (json/parse-string true)))

(println "\nSpringfield Incidents 🚦")
(println "------------------------")
(pprint incidents)

;; 3. Filter by severity.
;;    Get only the incidents where :severity is "critical".

(defn critical-alerts
  [incidents]
  (filter #(= (:severity %) "critical") incidents))

(println "\nCritical Alerts 🚨")
(println "------------------------")
(pprint (critical-alerts incidents))

;; 4. Group and count.
;;    Group all incidents by :source, then count how many per source.
;;    Hint: (frequencies (map :source incidents)) is a shortcut worth knowing.

(println "\nIncidents by Source 🏪")
(println "------------------------")
(pprint (frequencies (map :source incidents)))

;; 5. Post a new incident.
;;    Use (http/post url opts) to call POST /incidents.
;;    opts should be {:headers {"Content-Type" "application/json"} :body (json/generate-string payload)}
;;    Check the response :status is 201.

(def new-incident-response
  @(http/post
    (base-url "/incidents")
    {:headers {"Content-Type" "application/json"}
     :body (json/generate-string {:reporter "Lisa Simpson"
                                  :source   "springfield-elementary"
                                  :severity "high"
                                  :description "Science fair volcano exploded, lab evacuated"})}))

(println "\nNew Incident 🆕")
(println "------------------------")
(pprint new-incident-response)

;; 6. Retry on failure.
;;    Write a fn `fetch-with-retry` that retries GET /incidents up to 3 times if the status is 500.
;;    Return the parsed body on success, or throw an exception after all retries are exhausted.
;;    Hint: recursion in Clojure uses `loop/recur` — (loop [n 3] (if (= n 0) (throw ...) (recur (dec n))))
;;    Scala analogy: tail-recursive loop with an accumulator.

(println "\nFetch with Retry ⚡️")
(println "------------------------")
(defn fetch-with-retry
  ([path] (fetch-with-retry path 3))
  ([path attempts]
   (let [response @(http/get (base-url path))
         try-again? (> attempts 0)]
     (cond
       (= 200 (:status response))
       (-> response :body (json/parse-string true))

       (not try-again?)
       (throw (Exception. "all retry attempts failed :/"))

       :else (do (println  "trying again in 1 second... ")
                 (Thread/sleep 1000) ;; 1s
                 (recur path (dec attempts)))))))

(pprint (fetch-with-retry "/incidents"))

;; 7. Challenge.
;;    Write a fn `critical-by-source` that:
;;    - fetches all incidents (with retry)
;;    - returns a map of {source -> [reporter ...]} for critical incidents only
;;    Expected: {"springfield-nuclear" ["Homer Simpson" "Homer Simpson"]}

(println "\nEvents By Source ☢️️")
(println "------------------------")
(defn critical-by-source []
  (->> (fetch-with-retry "/incidents")
       critical-alerts
       (group-by :source)
       (map (fn [[k v]] {k (vec (map :reporter v))}))
       (apply merge))) ;; list of maps, to single map

(pprint (critical-by-source))
