<img src="https://clojure.org/images/clojure-logo-120b.png" align="right" height="64px"/>

# Clojure Lab λ

![Clojure](https://img.shields.io/badge/Clojure-5881D8?style=flat&logo=clojure&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat&logo=opensourceinitiative&logoColor=white)

Personal [Clojure λ](https://clojure.org/) learning sandbox.

## Structure

```text
.
├── .claude/
│   ├── agents/         claude code subagents (e.g. lisa)
│   └── rules/          clojure conventions & concepts
├── .github/
│   ├── actions/        ci: composite setup action
│   └── workflows/      ci: unit tests, lint, build
├── snippets/           quick experiments + exercises
└── pocs/
    ├── dispatch-api/   rest-api: leiningen, ring, compojure, dynamodb
    └── dispatch-ui/    front end: static html/css/js, calls dispatch-api
```

## Code Review

This project includes a `lisa` Claude Code subagent specialised in Clojure. 
It checks for functional purity, side effects, lazy sequence issues, and REST API patterns.

To invoke it from Claude Code 👇

```
Ask lisa to review <file or description>
```

## Personal Wiki

<details>
<summary>Notes I created during my learning process...</summary>

| Topic | Covers |
|-------|--------|
| [CLI](.claude/rules/clojure-cli.md) | `clojure` flags, `deps.edn` aliases |
| [Concepts](.claude/rules/clojure-concepts.md) | Pure functions, EDN, `if` x `when` x `cond`, recursion |
| [Conventions](.claude/rules/clojure-conventions.md) | Naming, namespaces, functions, data, idioms, `def` vs `defn`, comments |
| [Destructuring](.claude/rules/clojure-destructuring.md) | Vectors, maps, function params |
| [Polymorphism](.claude/rules/clojure-polymorphism.md) | Multimethods, protocols |
| [REPL on VSCode](.docs/repl-vscode.md) | Calva jack-in, eval loop, `comment` blocks |
| [Spec](.claude/rules/clojure-spec.md) | Where to create specs, snippets, Plumatic Schema alternative |

</details>

---

[How to install Clojure?] / [Try Clojure Online] / [How to install Leiningen?] / [The Reference ⭐️]

[How to install Clojure?]: https://clojure.org/guides/install_clojure
[Try Clojure Online]: https://tryclojure.org/
[How to install Leiningen?]: https://leiningen.org/
[The Reference ⭐️]: https://clojure.org/reference/reader
