const API_URL = "http://localhost:8080/api";
const BASE_URL = `${API_URL}/v1`;
const ENV_LABELS = { docker: "Docker", local: "Local" };
const CUSTOM_SOURCE = "__custom__";

/* A null status (network error) falls through to the error style */
const statusClass = (status) => {
  if (status >= 200 && status < 300) return "is-ok";
  if (status >= 400 && status < 500) return "is-client-error";
  return "is-server-error";
};

/* Indents valid JSON, returns anything else untouched */
const prettyJson = (text) => {
  try {
    return JSON.stringify(JSON.parse(text), null, 2);
  } catch (_) {
    return text;
  }
};

document.addEventListener("alpine:init", () => {
  Alpine.data("dispatchApp", () => ({
    tabs: [
      { id: "create", label: "File Report", icon: "report_problem" },
      { id: "list", label: "All Incidents", icon: "list_alt" },
      { id: "lookup", label: "Lookup by ID", icon: "search" },
      { id: "source", label: "By Source", icon: "factory" }
    ],
    tab: "create",
    sources: ["springfield-nuclear", "kwik-e-mart", "moes-tavern", "springfield-elementary", "duff-brewery", "city-hall"],
    customSource: CUSTOM_SOURCE,
    filterSource: "springfield-nuclear",
    filterSourceCustom: "",
    lookupId: "",
    createJson: JSON.stringify(
      {
        reporter: "Homer Simpson",
        source: "springfield-nuclear",
        severity: "critical",
        description: "Donut stuck in reactor panel"
      },
      null,
      2
    ),
    createJsonError: "",
    pending: { create: false, list: false, lookup: false, source: false },
    latestRequestId: 0,
    response: null,
    apiVersion: null,
    apiEnvironment: null,
    apiStatus: "checking",

    /* Alpine calls init() on load */
    init() {
      this.$nextTick(() => this.resizeJsonEditor());
      this.$watch("tab", () => this.$nextTick(() => this.resizeJsonEditor()));
      this.loadVersion();
    },

    /* Asks the API which version and environment it is running */
    async loadVersion() {
      try {
        const res = await fetch(`${API_URL}/version`);
        if (!res.ok) throw new Error(`API returned ${res.status}`);
        const info = await res.json();
        this.apiVersion = info.version ? `v${info.version.replace(/^v/, "")}` : null;
        this.apiEnvironment = ENV_LABELS[info.environment] || info.environment || "Unknown";
        this.apiStatus = "online";
      } catch (_) {
        this.apiStatus = "offline";
      }
    },

    focusTab(id) {
      this.tab = id;
      this.$nextTick(() => document.getElementById(`tab-${id}`).focus());
    },

    /* Arrow keys: delta is +1 (next) or -1 (previous), wrapping around */
    moveTab(delta) {
      const index = this.tabs.findIndex((t) => t.id === this.tab);
      this.focusTab(this.tabs[(index + delta + this.tabs.length) % this.tabs.length].id);
    },

    /* Grows the request body textarea to fit its content (only measurable while visible) */
    resizeJsonEditor() {
      const element = this.$refs.createEditor;
      if (!element || this.tab !== "create") return;
      element.style.height = "auto";
      element.style.height = `${element.scrollHeight + element.offsetHeight - element.clientHeight}px`;
    },

    formatJson() {
      try {
        this.createJson = JSON.stringify(JSON.parse(this.createJson), null, 2);
        this.createJsonError = "";
        this.$nextTick(() => this.resizeJsonEditor());
      } catch (err) {
        this.createJsonError = err.message;
      }
    },

    get canLookup() {
      return this.lookupId.trim() !== "";
    },

    /* hljs escapes its output, so the result is safe to render with x-html */
    highlight(text) {
      return hljs.highlight(text, { language: "json", ignoreIllegals: true }).value;
    },

    effectiveSource() {
      return this.filterSource === CUSTOM_SOURCE ? this.filterSourceCustom : this.filterSource;
    },

    /* Sends a request, times it, and stores the result for the response panel to render */
    async send(action, method, path, body) {
      const url = BASE_URL + path;
      const hasBody = body !== undefined;

      this.pending = { ...this.pending, [action]: true };
      const requestId = ++this.latestRequestId;
      const start = performance.now();
      let outcome;
      try {
        const res = await fetch(url, {
          method,
          headers: hasBody ? { "Content-Type": "application/json" } : undefined,
          body
        });
        outcome = {
          statusLabel: `${res.status} ${res.statusText || ""}`.trim(),
          statusClass: statusClass(res.status),
          body: prettyJson(await res.text()) || "(empty body)",
          error: null
        };
      } catch (err) {
        outcome = {
          statusLabel: "NETWORK ERROR",
          statusClass: statusClass(null),
          body: null,
          error: `${err.name}: ${err.message}\n\nIs the API running? (lein run / docker compose up)`
        };
      }
      const elapsed = `${Math.round(performance.now() - start)} ms`;

      this.pending = { ...this.pending, [action]: false };
      if (requestId === this.latestRequestId) {
        this.response = {
          ...outcome,
          summary: `${outcome.statusLabel}, ${elapsed}`,
          elapsed,
          request: `${method} ${url}`,
          requestBody: hasBody ? prettyJson(body) : null
        };
      }
    },

    fileReport() {
      this.send("create", "POST", "/incidents", this.createJson);
    },
    listAll() {
      this.send("list", "GET", "/incidents");
    },
    lookupById() {
      if (!this.canLookup) return;
      this.send("lookup", "GET", `/incidents/${encodeURIComponent(this.lookupId.trim())}`);
    },
    filterBySource() {
      this.send("source", "GET", `/incidents?source=${encodeURIComponent(this.effectiveSource())}`);
    }
  }));
});
