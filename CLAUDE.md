## Who & Why

Senior data engineer (Python, PySpark) learning Clojure to contribute to data ingestion APIs.  
This repo is the learning lab — prioritize clarity and teaching over cleverness.

When explaining Clojure: assume strong FP intuition from Python/PySpark, zero Lisp/Clojure syntax experience. 
Anchor explanations to Python/PySpark analogues. 
Be terse.

## Repo Layout

```text
snippets/   loose .clj files — REPL experiments, no build tool
pocs/       self-contained mini-projects, each with its own build file
```

## How to run

**snippets** — `clj -M snippets/<file>.clj` or `load-file` in a REPL  
**pocs** — each project uses Leiningen or Clojure CLI; check its own `project.clj` / `deps.edn`

