;;; Exercise 04
;;;     clojure.spec
;;;     clojure.spec lets you describe the shape of data and validate it.
;;;     Python analogy: like combining type hints with a runtime validator (e.g. pydantic),
;;;     but data-first — specs are registered globally by keyword, not tied to a class.
;;;
;;;     No extra deps — spec is built into Clojure.
(ns ex04-spec
  (:require [clojure.spec.alpha :as s]
            [clojure.pprint :refer [pprint]]))

;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Define individual field specs.
;;    Use s/def to register specs for each incident field.
;;    Hints:
;;    - string? is a built-in predicate
;;    - Sets work as predicates: (valid-severities "low") => "low" (truthy)
;;    - pos-int? matches positive integers
(def valid-severities #{"low" "medium" "high" "critical"})

(def valid-sources    #{"kwik-e-mart" "springfield-nuclear" "springfield-pd"
                        "springfield-fire" "moe-tavern" "springfield-elementary"})

;; s/def registers a spec globally under a namespaced keyword.
(s/def ::id ; is shorthand for :ex04-spec/id (current ns prefix).
       (s/and string? #(re-matches #"\d+" %)))
(s/def ::reporter string?)
(s/def ::source valid-sources)
(s/def ::severity valid-severities)
(s/def ::description string?)


;; 2. Define a map spec for the full incident.
;;    s/keys declares which keys are required (:req) or optional (:opt).
(s/def ::incident
       ; :req-un = "required, unqualified."
       ; It tells s/keys to check for the key by name only, ignoring namespace.
       ; Otherwise the dict must use ::id (which won't happen in real scenarios, json parse, etc)
       (s/keys :req-un [::id ::reporter ::source ::severity]
               :opt-un [::description]))

;; 3. Validate with s/valid?
;;    Returns true/false — same as calling a predicate.
;;    Try a valid incident and an invalid one (missing field, wrong severity).
(def mock-incident
  {:id "1"
   :reporter "Homer Simpson"
   :source "springfield-nuclear"
   :severity "critical"
   :description "Donut stuck in reactor panel"})

(println "\nEx. 03")
(println (s/valid? ::incident mock-incident))                                   ;; expected: ✅
(println (s/valid? ::incident (dissoc mock-incident :description)))             ;; expected: ✅
(println (s/valid? ::incident (dissoc mock-incident :severity)))                ;; expected: ❌
(println (s/valid? ::incident (assoc mock-incident :id "not-a-number")))        ;; expected: ❌
(println (s/valid? ::incident (assoc mock-incident :severity "easy-peasy")))    ;; expected: ❌


;; 4. Explain failures with s/explain-str.
;;    Returns a human-readable string describing what failed and why.
;;    Much more useful than a bare false when debugging a bad payload.
(println "\nEx. 04")
(println (s/explain-str ::incident (assoc mock-incident :id "not-a-number")))
(println (s/explain-str ::incident (dissoc mock-incident :severity)))


;; 5. Conform.
;;    s/conform returns the value if valid, or :clojure.spec.alpha/invalid if not.
;;    Useful when you want to both validate and use the value in one step.
;;    Python analogy: returning either the parsed value or the errors, e.g. a (value, errors)
;;    tuple — Python has no built-in Either type.
(println "\nEx. 05")
(println (s/conform ::incident mock-incident))
(println (s/conform ::incident (assoc mock-incident :id "not-a-number")))


;; 6. Challenge — validate a seq of incidents.
;;    Write a fn `validate-all` that takes a seq of maps and returns a map:
;;    {:valid [...]  :invalid [...]}
;;    splitting incidents into those that pass ::incident and those that don't.
(defn validate-all [incidents]
  (merge {:valid [] :invalid []} ; to make sure we always have both keys
         (group-by #(if (s/valid? ::incident %) :valid :invalid) incidents)))

(println "\nEx. 06")
(pprint (validate-all [mock-incident
                       (dissoc mock-incident :description)
                       (dissoc mock-incident :severity)
                       (assoc mock-incident :id "not-a-number")
                       (assoc mock-incident :severity "easy-peasy")]))
