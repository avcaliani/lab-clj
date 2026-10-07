<div align="center">

# `dispatch-ui`

![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?logo=javascript&logoColor=black)
![Alpine.js](https://img.shields.io/badge/Alpine.js-8BC0D0?logo=alpinedotjs&logoColor=black)

Front end for the Springfield incident reports.  
A single static page that calls [`dispatch-api`](../dispatch-api/README.md) and shows the raw response.

</div>

## Features

| Tab | Calls |
|---|---|
| (page load) | `GET /api/version`, shown under the title |
| File Report | `POST /api/v1/incidents` with an editable JSON body |
| All Incidents | `GET /api/v1/incidents` |
| Lookup by ID | `GET /api/v1/incidents/:id` |
| By Source | `GET /api/v1/incidents?source=<src>` |

The response panel shows status, elapsed time, the request sent and the pretty-printed body.

## Folder Structure

```text
.
├── index.html  ← layout and Alpine.js bindings
├── style.css   ← styles
├── app.js      ← Alpine component, fetch calls, response rendering
└── img/        ← images
```

**References**
- [Alpine.js](https://alpinejs.dev/) — reactive state in the HTML, loaded from a CDN
- [highlight.js](https://highlightjs.org/) — JSON syntax highlighting, loaded from a CDN

## Configuration

| Constant   | Default                        | Where       |
|------------|--------------------------------|-------------|
| `API_URL`  | `http://localhost:8080/api`    | `app.js`    |
| `BASE_URL` | `${API_URL}/v1`                | `app.js`    |

## Commands

No build step. Start the API first (see [`dispatch-api`](../dispatch-api/README.md)), then open the page.

```bash
# Start the API (default port: 8080)
cd ../dispatch-api && lein run

# Open the UI
open index.html
```
