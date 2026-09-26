const BASE_URL = "http://localhost:8080/api/v1";

document.addEventListener("alpine:init", () => {
  Alpine.data("dispatchApp", () => ({
    tab: "create",
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
    pending: {},
    latestRequestId: 0,
    response: null,

    formatJson() {
      try {
        this.createJson = JSON.stringify(JSON.parse(this.createJson), null, 2);
        this.createJsonError = "";
      } catch (err) {
        this.createJsonError = err.message;
      }
    },

    effectiveSource() {
      return this.filterSource === "__custom__" ? this.filterSourceCustom : this.filterSource;
    },

    statusClass(status) {
      if (status === null) return "status-network-error";
      if (status >= 200 && status < 300) return "status-ok";
      if (status >= 400 && status < 500) return "status-client-error";
      return "status-server-error";
    },

    /* Sends a request, times it, and stores the result for the response panel to render */
    async send(action, method, path, body) {
      const url = BASE_URL + path;
      let requestBody = null;
      if (body !== undefined) {
        try {
          requestBody = JSON.stringify(JSON.parse(body), null, 2);
        } catch (_) {
          requestBody = body;
        }
      }

      this.pending = { ...this.pending, [action]: true };
      const requestId = ++this.latestRequestId;
      const start = performance.now();
      try {
        const res = await fetch(url, {
          method,
          headers: body !== undefined ? { "Content-Type": "application/json" } : undefined,
          body: body !== undefined ? body : undefined
        });
        const elapsedMs = Math.round(performance.now() - start);
        const text = await res.text();
        let pretty = text;
        try {
          pretty = JSON.stringify(JSON.parse(text), null, 2);
        } catch (_) {
          /* not JSON, show raw */
        }
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
            if (this.$refs.reqCode) hljs.highlightElement(this.$refs.reqCode);
            if (this.$refs.resCode) hljs.highlightElement(this.$refs.resCode);
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
      this.send("lookup", "GET", `/incidents/${encodeURIComponent(this.lookupId)}`);
    },
    filterBySource() {
      this.send("source", "GET", `/incidents?source=${encodeURIComponent(this.effectiveSource())}`);
    }
  }));
});
