/*
 * Random incident generator for the File Report tab.
 * Plain classic script (no modules), loaded before app.js. Exposes one global: IncidentGenerator.
 *
 *   IncidentGenerator.random()                       -> { json, mutation }
 *   IncidentGenerator.random({ failureRate: 1 })     -> always broken
 *
 * `json` is the request body as a string (it may be deliberately invalid JSON).
 * `mutation` is null for a valid payload, otherwise a label like "wrong-type(severity)".
 */
const IncidentGenerator = (() => {
  const sources = [
    "kwik-e-mart", "springfield-nuclear", "springfield-pd", "springfield-fire",
    "moe-tavern", "springfield-elementary", "springfield-hospital"
  ];
  const reporters = [
    "Homer Simpson", "Marge Simpson", "Bart Simpson", "Lisa Simpson", "Ned Flanders",
    "Apu Nahasapeemapetilon", "Chief Wiggum", "Moe Szyslak", "Mr. Burns", "Waylon Smithers"
  ];
  const severities = ["low", "medium", "high", "critical"];
  const descriptions = [
    "Donut stuck in reactor panel",
    "Squishee machine leaking blue liquid",
    "Three-eyed fish spotted in the cooling tower",
    "Bart prank-called every bar in town",
    "Bell tower stuck ringing out of tune",
    "Fire hydrant opened by a runaway pig"
  ];
  const requiredFields = ["reporter", "source", "severity", "description"];
  const serverFields = ["id", "status", "created_at", "ended_at", "end_reason"];

  const pick = (list) => list[Math.floor(Math.random() * list.length)];
  const withField = (payload, field, value) => ({ ...payload, [field]: value });

  const valid = () => ({
    reporter: pick(reporters),
    source: pick(sources),
    severity: pick(severities),
    description: pick(descriptions)
  });

  /* Each mutator takes a valid payload and returns { body, label }. Body: object or raw string. */
  const mutators = [
    (p) => {
      const field = pick(requiredFields);
      const { [field]: _, ...rest } = p;
      return { body: rest, label: `missing-field(${field})` };
    },
    (p) => ({ body: withField(p, "flux_capacitor", 1), label: "unknown-field(flux_capacitor)" }),
    (p) => {
      const [field, value] = pick([["severity", 3], ["description", null], ["reporter", ["Homer"]], ["source", { name: "x" }]]);
      return { body: withField(p, field, value), label: `wrong-type(${field})` };
    },
    (p) => ({ body: withField(p, "severity", pick(["apocalyptic", "CRITICAL", ""])), label: "bad-enum(severity)" }),
    (p) => ({ body: withField(p, "source", "evergreen-terrace"), label: "unknown-source" }),
    (p) => ({
      body: withField(p, "source", pick([` ${p.source} `, p.source.toUpperCase(), p.source.replace(/-/g, "_")])),
      label: "source-format-drift"
    }),
    (p) => ({ body: withField(p, "reporter", "Sideshow Bob"), label: "unknown-reporter" }),
    (p) => {
      const field = pick(["description", "reporter"]);
      return { body: withField(p, field, pick(["", "   "])), label: `blank-string(${field})` };
    },
    (p) => ({ body: withField(p, "description", "x".repeat(10000)), label: "oversized(description)" }),
    (p) => ({
      body: withField(p, "description", pick(["<script>alert(1)</script>", "'; DROP TABLE x;--", "🍩🔥 line1\nline2"])),
      label: "odd-text(description)"
    }),
    (p) => ({ body: withField(p, pick(serverFields), "client-supplied"), label: "server-owned-field" }),
    () => ({ body: pick(["[]", "\"hi\"", "null", ""]), label: "wrong-top-level-shape" }),
    (p) => ({ body: JSON.stringify(p, null, 2).replace(/\n}$/, ",\n}"), label: "malformed-json(trailing-comma)" }),
    (p) => ({ body: JSON.stringify(p, null, 2).replace(/\n}$/, ""), label: "malformed-json(unclosed-brace)" })
  ];

  const random = ({ failureRate = 0.3 } = {}) => {
    const payload = valid();
    if (Math.random() >= failureRate) {
      return { json: JSON.stringify(payload, null, 2), mutation: null };
    }
    const { body, label } = pick(mutators)(payload);
    return {
      json: typeof body === "string" ? body : JSON.stringify(body, null, 2),
      mutation: label
    };
  };

  return { sources, random };
})();
