<img src="https://clojure.org/images/clojure-logo-120b.png" align="right" height="64px"/>

# Clojure Lab λ

![Clojure](https://img.shields.io/badge/Clojure-5881D8?style=flat&logo=clojure&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat&logo=opensourceinitiative&logoColor=white)

Personal [Clojure λ](https://clojure.org/) learning sandbox.

## Structure

| Directory        | Purpose |
|------------------|---------|
| `snippets/`      | Loose `.clj` files — quick experiments and concept tests, meant to be loaded in a REPL |
| `pocs/`          | Self-contained mini-projects, each with its own build file (`project.clj` or `deps.edn`) |
| `.claude/rules/` | Clojure conventions and concepts distilled from personal research — auto-loaded by Claude Code when working with `.clj` files |

## Code Review

This project includes a `code-reviewer` Claude Code subagent specialised in Clojure. 
It checks for functional purity, side effects, lazy sequence issues, and REST API patterns.

To invoke it from Claude Code 👇

```
Use the code-reviewer agent to review <file or description>
```

---

[How to install Clojure?] / [Try Clojure Online]

[How to install Clojure?]: https://clojure.org/guides/install_clojure
[Try Clojure Online]: https://tryclojure.org/
