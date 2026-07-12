;;; Exercise 04
;;;     clojure.spec
;;;     clojure.spec lets you describe the shape of data and validate it.
;;;     Python analogy: like combining type hints with a runtime validator (e.g. pydantic),
;;;     but data-first — specs are registered globally by keyword, not tied to a class.
;;;
;;;     No extra deps — spec is built into Clojure.
(ns ex04-spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

;; s/def registers a spec globally under a namespaced keyword.
;; ::incident/id is shorthand for :ex04-spec/incident-id (current ns prefix).
;; Python analogy: think of it as a named schema entry (like a pydantic field), not a type.

(def valid-severities #{"low" "medium" "high" "critical"})
(def valid-sources    #{"kwik-e-mart" "springfield-nuclear" "springfield-pd"
                        "springfield-fire" "moe-tavern" "springfield-elementary"})


;; ─── EXERCISES ───────────────────────────────────────────────────────────────

;; 1. Define individual field specs.
;;    Use s/def to register specs for each incident field.
;;    Hints:
;;    - string? is a built-in predicate
;;    - Sets work as predicates: (valid-severities "low") => "low" (truthy)
;;    - pos-int? matches positive integers

;; (s/def ::id          ...)
;; (s/def ::reporter    ...)
;; (s/def ::source      ...)
;; (s/def ::severity    ...)
;; (s/def ::description ...)


;; 2. Define a map spec for the full incident.
;;    s/keys declares which keys are required (:req) or optional (:opt).
;;    Note: keys must be namespaced (::id, not :id).

;; (s/def ::incident
;;   (s/keys :req [...]))


;; 3. Validate with s/valid?
;;    Returns true/false — same as calling a predicate.
;;    Try a valid incident and an invalid one (missing field, wrong severity).

;; (s/valid? ::incident {:id "1" :reporter "Homer" ...})


;; 4. Explain failures with s/explain-str.
;;    Returns a human-readable string describing what failed and why.
;;    Much more useful than a bare false when debugging a bad payload.

;; (s/explain-str ::incident {:id "1" :severity "catastrophic"})


;; 5. Conform.
;;    s/conform returns the value if valid, or :clojure.spec.alpha/invalid if not.
;;    Useful when you want to both validate and use the value in one step.
;;    Python analogy: returning either the parsed value or the errors, e.g. a (value, errors)
;;    tuple — Python has no built-in Either type.

;; (s/conform ::incident some-map)


;; 6. Challenge — validate a seq of incidents.
;;    Write a fn `validate-all` that takes a seq of maps and returns a map:
;;    {:valid [...]  :invalid [...]}
;;    splitting incidents into those that pass ::incident and those that don't.
;;    Use the incidents from ex02_server.clj as test data.
