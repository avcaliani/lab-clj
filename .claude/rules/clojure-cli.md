---
paths:
  - "**/*.clj"
---

<img src="https://clojure.org/images/clojure-logo-120b.png" align="right" height="64px"/>

# Clojure CLI

Jump to: [Flags](#flags) · [Aliases](#aliases)


---

## Flags

| Flag      | What it does                             |
|-----------|------------------------------------------|
| `-Srepro` | Ignore user-level `~/.clojure/deps.edn`. |
| `-Stree`  | Print the dependency tree and exit.      |

```bash
clj -Srepro -Stree   # reproducible dependency tree
```

---

## Aliases

An alias is a named block in `deps.edn` (extra deps, paths, main opts).
Select it with `:name` after the flag.

```clojure
;; deps.edn (illustrative)
{:paths ["src"]                                     ; always on the classpath
 :deps  {org.clojure/clojure {:mvn/version "1.12.0"}} ; always loaded

 :aliases
 {;; :dev adds things only you need while developing
  :dev  {:extra-paths ["dev"]                       ; + dev/ folder
         :extra-deps  {nrepl/nrepl {:mvn/version "1.3.0"}}}

  ;; :test adds test code, a runner, and says what to run
  :test {:extra-paths ["test"]
         :extra-deps  {io.github.cognitect-labs/test-runner
                       {:git/tag "v0.5.1" :git/sha "dfb30dd"}}
         :main-opts   ["-m" "cognitect.test-runner"]}}}
```

Top-level `:paths` and `:deps` always apply. An alias layers extras on top, only when you ask for it.

```bash
clojure -A:dev          # REPL with :dev extras on the classpath
clojure -M:test         # run :test main-opts (the test runner)
clojure -M:dev:test     # stack aliases, merged left to right
```

- `-A` only adjusts the classpath (REPL).
- `-M` also runs the alias `:main-opts` (or a file/`-m` you pass).
