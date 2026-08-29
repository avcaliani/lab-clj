;;; ANSI colors + tag helper, shared across snippets.

(ns colors)

(def ansi-colors
  {:red    "[31m"
   :green  "[32m"
   :yellow "[33m"
   :cyan   "[36m"
   :bold   "[1m"
   :reset  "[0m"})

(defn tag [color label]
  (str (ansi-colors color) "[" label "]" (ansi-colors :reset)))

(defn colorize [color value]
  (str (ansi-colors color) value (ansi-colors :reset)))
