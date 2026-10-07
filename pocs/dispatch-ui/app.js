const API_URL = "http://localhost:8080/api";
const BASE_URL = `${API_URL}/v1`;
const ENV_LABELS = { docker: "Docker", local: "Local" };
const CUSTOM_SOURCE = "__custom__";

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

    /* Alpine calls init() on load: ask the API which version it is running */
    async init() {
      try {
        const res = await fetch(`${API_URL}/version`);
        if (!res.ok) throw new Error(`API returned ${res.status}`);
        const info = await res.json();
        this.apiVersion = info.version ? `v${info.version.replace(/^v/, "")}` : null;
        this.apiEnvironment = ENV_LABELS[info.environment] || info.environment || "Unknown";
        this.apiStatus = "online";
      } catch (_) {
        this.apiVersion = null;
        this.apiEnvironment = null;
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

    resizeJsonEditor(element) {
      element.style.height = "auto";
      element.style.height = `${element.scrollHeight + element.offsetHeight - element.clientHeight}px`;
    },

    formatJson() {
      try {
        this.createJson = JSON.stringify(JSON.parse(this.createJson), null, 2);
        this.createJsonError = "";
      } catch (err) {
        this.createJsonError = err.message;
      }
    },

    effectiveSource() {
      return this.filterSource === CUSTOM_SOURCE ? this.filterSourceCustom : this.filterSource;
    },

    /* A null status (network error) falls through to the error style */
    statusClass(status) {
      if (status >= 200 && status < 300) return "status-ok";
      if (status >= 400 && status < 500) return "status-client-error";
      return "status-server-error";
    },

    /* Sends a request, times it, and stores the result for the response panel to render */
    async send(action, method, path, body) {
      const url = BASE_URL + path;
      const hasBody = body !== undefined;
      const requestBody = hasBody ? prettyJson(body) : null;

      this.pending = { ...this.pending, [action]: true };
      const requestId = ++this.latestRequestId;
      const start = performance.now();
      try {
        const res = await fetch(url, {
          method,
          headers: hasBody ? { "Content-Type": "application/json" } : undefined,
          body
        });
        const elapsedMs = Math.round(performance.now() - start);
        const pretty = prettyJson(await res.text());
        if (requestId === this.latestRequestId) {
          this.response = {
            status: res.status,
            statusLabel: `${res.status} ${res.statusText || ""}`.trim(),
            elapsedMs,
            requestMethod: method,
            requestUrl: url,
            requestBody,
            body: pretty || "(empty body)",
            error: null
          };
        }
      } catch (err) {
        if (requestId === this.latestRequestId) {
          this.response = {
            status: null,
            statusLabel: "NETWORK ERROR",
            elapsedMs: Math.round(performance.now() - start),
            requestMethod: method,
            requestUrl: url,
            requestBody,
            body: null,
            error: `${err.name}: ${err.message}\n\nIs the API running? (lein run / docker compose up)`
          };
        }
      } finally {
        this.pending = { ...this.pending, [action]: false };
        if (requestId === this.latestRequestId) {
          this.$nextTick(() => {
            [this.$refs.reqCode, this.$refs.resCode].filter(Boolean).forEach((el) => {
              /* Alpine reuses the node and highlight.js skips nodes it already marked */
              delete el.dataset.highlighted;
              hljs.highlightElement(el);
            });
          });
        }
      }
    },

    fileReport() {
      this.send("create", "POST", "/incidents", this.createJson);
    },
    listAll() {
      this.send("list", "GET", "/incidents");
    },
    lookupById() {
      if (!this.lookupId.trim()) return;
      this.send("lookup", "GET", `/incidents/${encodeURIComponent(this.lookupId)}`);
    },
    filterBySource() {
      this.send("source", "GET", `/incidents?source=${encodeURIComponent(this.effectiveSource())}`);
    }
  }));
});
