# CLAUDE.md

## Who & Why

Senior data engineer (Python, Scala, Spark) learning Clojure to contribute to data ingestion APIs.  
This repo is the learning lab — prioritize clarity and teaching over cleverness.

When explaining Clojure: assume strong FP intuition from Scala/Spark, zero Lisp/Clojure syntax experience. 
Anchor explanations to Scala/Spark analogues. 
Be terse.

## Repo Layout

```
snippets/   loose .clj files — REPL experiments, no build tool
pocs/       self-contained mini-projects, each with its own build file
```

## How to run

**snippets** — `clj snippets/<file>.clj` or `load-file` in a REPL  
**pocs** — each project uses Leiningen or Clojure CLI; check its own `project.clj` / `deps.edn`

## Reference docs

Before starting a task, check `.docs/` for relevant context:

- `.docs/clojure-conventions.md` — naming, namespace layout, idiomatic patterns
