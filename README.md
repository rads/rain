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

- [Installation](#installation)
- [Docs](#docs)
- [Usage](#usage)

## Installation

Add `io.github.rads/rain` to your `deps.edn`:

```clojure
io.github.rads/rain {:git/tag "v0.1.5" :git/sha "e38aead"}
```

## Docs

- [API Docs](docs/api.md)

## Usage

Rain supports multiple rendering patterns. See the table below to find an example based on your needs.

<table>
  <thead>
    <tr>
      <td></td>
      <td><strong>SSG</strong></td>
      <td><strong>ISG</strong></td>
      <td><strong>CSR</strong></td>
      <td><strong>SSR</strong></td>
      <td><strong>Hydration</strong></td>
    </tr>
  </thead>
<tbody>
  <tr>
    <td><strong>Example</strong></td>
    <td><a href="http://github.com/rads/rain.examples.ssg">rain.examples.ssg</a></td>
    <td><a href="http://github.com/rads/bbin-site">bbin-site</a></td>
    <td><a href="#">TODO</a></td>
    <td><a href="#">TODO</a></td>
    <td><a href="http://github.com/rads/rain.examples.todomvc">rain.examples.todomvc</a></td>
  </tr>
  <tr>
    <td><strong>Use Cases</strong></td>
    <td>Landing pages, blogs</td>
    <td>Dashboards</td>
    <td>Full-featured apps</td>
    <td>Document-based sites</td>
    <td>Supports both CSR and SSR use cases</td>
  </tr>
  <tr>
    <td><strong>Host as a static site (JAMstack)?</strong></td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>No</td>
    <td>No</td>
  </tr>
  <tr>
    <td><strong>API required?</strong></td>
    <td>No</td>
    <td>No</td>
    <td>Yes</td>
    <td>No</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><strong>Custom server required?</strong></td>
    <td>No</td>
    <td>No</td>
    <td>No</td>
    <td>Yes</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><strong>JavaScript required for viewing?</strong></td>
    <td>No</td>
    <td>No</td>
    <td>Yes</td>
    <td>No</td>
    <td>No</td>
  </tr>
  <tr>
    <td><strong>JavaScript required for interaction?</strong></td>
    <td>No</td>
    <td>No</td>
    <td>Yes</td>
    <td>No</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><strong>Dynamic content on build?</strong></td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><strong>Dynamic content after deploy?</strong></td>
    <td>No</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><strong>Dynamic content based on request?</strong></td>
    <td>No</td>
    <td>No</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><strong>User login supported?</strong></td>
    <td>No</td>
    <td>No</td>
    <td>Yes</td>
    <td>Yes</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td><strong>First Content Paint (FCP)?</strong></td>
    <td>Good</td>
    <td>Good</td>
    <td>Bad</td>
    <td>Good</td>
    <td>Good</td>
  </tr>
  <tr>
    <td><strong>Time-to-Interactive (TTI)?</strong></td>
    <td>Good</td>
    <td>Good</td>
    <td>Bad</td>
    <td>Good</td>
    <td>Bad</td>
  </tr>
</tbody>
</table>

