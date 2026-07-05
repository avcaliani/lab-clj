## Who & Why

Senior data engineer (Python, PySpark) learning Clojure to contribute to data ingestion APIs.  
This repo is the learning lab — prioritize clarity and teaching over cleverness.

When explaining Clojure: assume strong FP intuition from Python/PySpark, zero Lisp/Clojure syntax experience. 
Anchor explanations to Python/PySpark analogues. 
Be terse.

## Repo Layout

```text
.
├── .github/
│   ├── actions/        ci: clojure setup
│   └── workflows/      ci: unit tests + lint + build
├── snippets/           loose .clj files — REPL experiments, no build tool
└── pocs/               self-contained mini-projects
    └── dispatch-api/   rest-api: leiningen, ring, compojure, dynamodb
```

## How to run

**snippets** — `clj -M snippets/<file>.clj` or `load-file` in a REPL  
**pocs**
 - check each `project.clj`
 - run unit test: `lein test`
 - run: `lein run`
 - fix formatting: `lein cljfmt fix`
 - lint: `lein clj-kondo --lint src test`

## Keep Docs Updated

During your work, if you notice incorrect or outdated information in Markdown files 
or code comments and docs, alert the user and suggest the update.
