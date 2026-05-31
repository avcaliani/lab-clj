# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

Personal Clojure exploration lab for a senior data engineer learning Clojure to contribute to data ingestion APIs.

## Repo structure

```
snippets/   loose .clj files — quick experiments, REPL-ready scripts, concept tests
pocs/       self-contained mini-projects, each in its own subdirectory with its own build file
```

### snippets/

No build tool. Files are meant to be loaded in a REPL or run directly:

```bash
clj snippets/whatever.clj          # run a file
# or load-file inside a REPL session
```

### pocs/

Each POC lives in its own directory as an independent project. Use Leiningen or Clojure CLI (deps.edn) per project — no constraint to pick one globally.

```bash
# Leiningen
cd pocs/my-api && lein repl
cd pocs/my-api && lein test
cd pocs/my-api && lein run

# Clojure CLI
cd pocs/my-api && clj -M:repl
cd pocs/my-api && clj -T:test
```
