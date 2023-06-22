# rain [![Slack](https://img.shields.io/badge/clojurians-rain-blue.svg?logo=slack)](https://clojurians.slack.com/messages/rain/)

**A Clojure/Script library for fast and flexible web apps.**

- Supports multiple rendering patterns in the same app:
    - Static Site Generation (SSG)
    - Incremental Static Generation (ISG)
    - Server-Side Rendering (SSR)
    - Client-Side Rendering (CSR)
    - Hydration
- Supports reusable code between the server and browser
- Helpers for biff, reitit, reagent, and re-frame
- Fast
- Extensible

**Status:** alpha

## Table of Contents

- [Usage](#usage)
- [Installation](#installation)
- [Docs](#docs)

## Usage

For a real-world example, check out the [bbin-site](https://bbin.rads.dev) and its [source code](https://github.com/rads/bbin-site).

## Installation

Add `io.github.rads/rain` to your `deps.edn`:

```clojure
io.github.rads/rain {:git/tag "v0.1.0" :git/sha "205ac6c"}
```

## Docs

- [API Docs](docs/api.md)
