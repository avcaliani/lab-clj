# REPL on VSCode

![Clojure](https://img.shields.io/badge/Clojure-5881D8?style=flat&logo=clojure&logoColor=white)
![Calva](https://img.shields.io/badge/Calva-DF793B?style=flat)

Install **[Calva](https://calva.io/)** extension.

> [!info]
> Calva looks for `deps.edn` or `project.clj` in the opened folder.

### Start the REPL (jack-in)

1. `Cmd+Shift+P` → **Calva: Start a Project REPL and Connect (aka Jack-In)**
2. Pick the project type:
   - `snippets` → `deps.edn`
   - `dispatch-api` → `Leiningen`
3. Wait for `nRaEPL server started` in the output panel.

### Eval Loop

```bash
"Ctrl+Option+C Enter"          # Load the whole file (like `import`)
"Option+Enter"                 # Eval the top-level form
"Cmd+Enter"                    # Eval the form before the cursor
"Ctrl+Option+C Ctrl+Option+N"  # Switch REPL to the current ns
```

Results show inline, next to the code.

### Use `(comment ...)` for experiments

Anything with side effects goes inside a `comment` block.
Eval each form by hand, nothing runs on file load.

```clojure
(comment
  (def server (start-server))
  (stop-server server)
  (parse-order {:id 1}))
```

---

### Start the nREPL Server Yourself?

Calva does all of this for you, but just in case you like to do it yourself.

**(1)** Start the server in a terminal

```bash
cd snippets/ && clojure \
          -Sdeps '{:deps {nrepl/nrepl {:mvn/version "1.3.1"}}}' \
          -M -m nrepl.cmdline --port "7888"

# dispatch-api (Leiningen, headless = no terminal prompt)
cd pocs/dispatch-api/ && lein repl :headless :port 7889
```

**(2)** `Cmd+Shift+P` → **Calva: Connect to a Running REPL Server in the Project**
**(3)** Pick the project type (`deps.edn` or `Leiningen`), then `localhost:<port>`

> [!warning]
> A bare nREPL has no `cider-nrepl` middleware, so Calva loses some features (completion, docs, test runner).
> Eval still works. Jack-in injects it for you.

Ref: [Launching a basic REPL](https://clojure.org/guides/repl/launching_a_basic_repl)
