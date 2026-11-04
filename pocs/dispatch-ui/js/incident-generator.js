/*
 * Random incident generator for the File Report tab.
 * Plain classic script, loaded before app.js. Exposes one global: IncidentGenerator.
 * Not an ES module on purpose: the page is opened from file://, where Chrome blocks module scripts.
 *
 *   IncidentGenerator.random()                       -> { json, label, valid }
 *   IncidentGenerator.random({ failureRate: 1 })     -> always broken
 *   IncidentGenerator.random({ rng: seededRandom })  -> reproducible (rng returns a number in [0, 1))
 *
 * `json` is the request body as a string (it may be deliberately invalid JSON).
 * `valid` is true for an untouched payload, otherwise `label` names the mutation, e.g. "wrong-type(severity)".
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

  const makePick = (rng) => (list) => list[Math.floor(rng() * list.length)];
  const setField = (payload, field, value) => ({ ...payload, [field]: value });

  const valid = (pick) => ({
    reporter: pick(reporters),
    source: pick(sources),
    severity: pick(severities),
    description: pick(descriptions)
  });

  /* Each mutator takes a valid payload and a pick function, and returns { body, label }. Body: object or raw string. */
  const mutators = [
    (p, pick) => {
      const field = pick(requiredFields);
      const body = Object.fromEntries(Object.entries(p).filter(([key]) => key !== field));
      return { body, label: `missing-field(${field})` };
    },
    (p) => ({ body: setField(p, "flux_capacitor", 1), label: "unknown-field(flux_capacitor)" }),
    (p, pick) => {
      const [field, value] = pick([["severity", 3], ["description", null], ["reporter", ["Homer"]], ["source", { name: "x" }]]);
      return { body: setField(p, field, value), label: `wrong-type(${field})` };
    },
    (p, pick) => ({ body: setField(p, "severity", pick(["apocalyptic", "CRITICAL", ""])), label: "bad-enum(severity)" }),
    (p) => ({ body: setField(p, "source", "evergreen-terrace"), label: "unknown-source" }),
    (p, pick) => ({
      body: setField(p, "source", pick([` ${p.source} `, p.source.toUpperCase(), p.source.replace(/-/g, "_")])),
      label: "source-format-drift"
    }),
    (p) => ({ body: setField(p, "reporter", "Sideshow Bob"), label: "unknown-reporter" }),
    (p, pick) => {
      const field = pick(["description", "reporter"]);
      return { body: setField(p, field, pick(["", "   "])), label: `blank-string(${field})` };
    },
    (p) => ({ body: setField(p, "description", "x".repeat(1000)), label: "oversized(description)" }),
    (p, pick) => ({
      body: setField(p, "description", pick(["<script>alert(1)</script>", "'; DROP TABLE x;--", "🍩🔥 line1\nline2"])),
      label: "odd-text(description)"
    }),
    (p, pick) => ({ body: setField(p, pick(serverFields), "client-supplied"), label: "server-owned-field" }),
    (_, pick) => ({ body: pick(["[]", "\"hi\"", "null", ""]), label: "wrong-top-level-shape" }),
    (p) => ({ body: JSON.stringify(p, null, 2).replace(/\n}$/, ",\n}"), label: "malformed-json(trailing-comma)" }),
    (p) => ({ body: JSON.stringify(p, null, 2).replace(/\n}$/, ""), label: "malformed-json(unclosed-brace)" })
  ];

  const random = ({ failureRate = 0.3, rng = Math.random } = {}) => {
    const pick = makePick(rng);
    const payload = valid(pick);
    if (rng() >= failureRate) {
      return { json: JSON.stringify(payload, null, 2), label: "valid", valid: true };
    }
    const { body, label } = pick(mutators)(payload, pick);
    return {
      json: typeof body === "string" ? body : JSON.stringify(body, null, 2),
      label,
      valid: false
    };
  };

  return { random };
})();
