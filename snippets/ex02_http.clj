;;; Exercise 02
;;;     HTTP basics
;;;     New concepts: http-kit client, cheshire (JSON parsing), namespaces + require.
;;;
;;;     require is Clojure's import. :as aliases the namespace — like Python's import x.y.z as z.
;;;     Keywords after require (:as, :refer) are options, not data.
(ns ex02-http
  (:require [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [org.httpkit.client :as http]))

(defn- base-url [path] (str "http://localhost:8080" path))

;; ─── EXERCISES ───────────────────────────────────────────────────────────────
;; 1. Fetch all incidents.
;;    Use (http/get url) to call GET /incidents.
;;    The call returns a promise — deref it with @ to get the response map.
;;    Hint: @(http/get url) gives you {:status 200 :body "...json string..."}
(def response @(http/get (base-url "/incidents")))

(println "\nEx. 01")
(pprint response)


;; 1.1. Handle failures.
;;    The server randomly returns 500. Check (:status response) and print a message if it failed.
;;    Hint: use `when` for a one-branch conditional — (when condition expr)
(when (not= 200 (:status response))
  (println "\n\nFailed to retrieve incidents :/")
  (println "Interrupting the script... See ya o/")
  (throw (Exception. "Failed to get incidents, try again!")))


;; 2. Parse the response body.
;;    The body is a JSON string. Use (json/parse-string body true) to get a seq of maps.
;;    The `true` argument keywordizes keys: "source" becomes :source.
(def incidents (-> response
                   :body
                   (json/parse-string true)))

(println "\nEx. 02")
(pprint incidents)


;; 3. Filter by severity.
;;    Get only the incidents where :severity is "critical".
(defn critical-alerts
  [incidents]
  (filter #(= (:severity %) "critical") incidents))

(println "\nEx. 03")
(pprint (critical-alerts incidents))


;; 4. Group and count.
;;    Group all incidents by :source, then count how many per source.
;;    Hint: (frequencies (map :source incidents)) is a shortcut worth knowing.
(println "\nEx. 04")
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

(if (= 201 (:status new-incident-response))
  (println "Incident Created ✅")
  (println "Incident NOT created ❌"))

(println "\nEx. 05")
(pprint new-incident-response)


;; 6. Retry on failure.
;;    Write a fn `fetch-with-retry` that retries GET /incidents up to 3 times if the status is 500.
;;    Return the parsed body on success, or throw an exception after all retries are exhausted.
;;    Hint: recursion in Clojure uses `loop/recur` — (loop [n 3] (if (= n 0) (throw ...) (recur (dec n))))
;;    Python analogy: a while loop counting down a retries variable.
(println "\nEx. 06")
(defn fetch-with-retry
  ([path] (fetch-with-retry path 3)) ;; python like -> def fetch_with_retry(path, remaining=3)
  ([path remaining]
   (if (zero? remaining)
     (throw (Exception. "all retry attempts failed :/"))
     (let [response @(http/get (base-url path))]
       (if (= 200 (:status response))
         (-> response :body (json/parse-string true))
         (do (println "trying again in 1 second... ") ;; `do` it's just to writing multiple commands
             (Thread/sleep 1000)
             (recur path (dec remaining))))))))

(pprint (fetch-with-retry "/incidents"))


;; 7. Challenge.
;;    Write a fn `critical-by-source` that:
;;    - fetches all incidents (with retry)
;;    - returns a map of {source -> [reporter ...]} for critical incidents only
;;    Expected: {"springfield-nuclear" ["Homer Simpson" "Homer Simpson"]}
(println "\nEx. 07")
(defn critical-by-source []
  (->> (fetch-with-retry "/incidents")
       critical-alerts
       (group-by :source)
       (map (fn [[k v]] {k (vec (map :reporter v))}))
       (apply merge))) ;; list of maps, to single map

(pprint (critical-by-source))
