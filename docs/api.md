# Table of contents
-  [`rain.biff`](#rain.biff)  - Functions for using Rain on top of Biff.
    -  [`use-chime`](#rain.biff/use-chime) - A Biff component for <code>chime</code>.
    -  [`use-jetty`](#rain.biff/use-jetty) - A Biff component for <code>jetty</code>.
    -  [`use-shadow-cljs`](#rain.biff/use-shadow-cljs) - A Biff component for <code>shadow-cljs</code>.
-  [`rain.core`](#rain.core)  - Functions to support rendering pages with Reitit and Hiccup.
    -  [`bootstrap-data-tag`](#rain.core/bootstrap-data-tag) - Returns a <code>&lt;script&gt;</code> tag in Hiccup format that encodes the <code>:rain/bootstrap-data</code> key from the Ring request as <code>application/transit+json</code>.
    -  [`csrf-meta-tag`](#rain.core/csrf-meta-tag) - Returns a <code>&lt;meta&gt;</code> tag for a CSRF token in Hiccup format if <code>:anti-forgery-token</code> is in the request.
    -  [`export-pages`](#rain.core/export-pages) - Export static pages to a directory.
    -  [`main-cljs-bundle-path`](#rain.core/main-cljs-bundle-path) - Returns the path to the main CLJS bundle if the manifest file exists.
    -  [`main-cljs-bundle-tag`](#rain.core/main-cljs-bundle-tag) - Returns a <code>&lt;script&gt;</code> tag for the main CLJS bundle if the manifest file exists.
    -  [`main-stylesheet-path`](#rain.core/main-stylesheet-path) - Returns the path of the main stylesheet if the manifest file exists.
    -  [`main-stylesheet-tag`](#rain.core/main-stylesheet-tag) - Returns the main stylesheet <code>&lt;link&gt;</code> tag if the main CSS file is found.
    -  [`meta-tags`](#rain.core/meta-tags) - Returns a list of <code>&lt;meta&gt;</code> tags in Hiccup format.
    -  [`script-tags`](#rain.core/script-tags) - Returns list of <code>&lt;script&gt;</code> tags in Hiccup format.
    -  [`site-routes`](#rain.core/site-routes) - Returns site routes wrapped with the <code>wrap-page</code> middleware.
    -  [`static-pages`](#rain.core/static-pages) - Return a map of page paths to page HTML, generated from the static routes.
    -  [`stylesheet-tags`](#rain.core/stylesheet-tags) - Returns a list of stylesheet <code>&lt;link&gt;</code> tags in Hiccup format.
    -  [`wrap-page`](#rain.core/wrap-page) - A Ring middleware that adds support for page config keys in route definitions.
-  [`rain.re-frame`](#rain.re-frame)  - Functions to support rendering Reagent and Re-frame components on both the JVM and browser.
    -  [`*app-db*`](#rain.re-frame/*app-db*) - A dynamic var for the current Re-frame app DB.
    -  [`atom`](#rain.re-frame/atom) - Returns a Reagent atom.
    -  [`current-page`](#rain.re-frame/current-page) - A Reagent component to render the current page.
    -  [`dispatch`](#rain.re-frame/dispatch) - Dispatch a Re-frame event asynchronously.
    -  [`dispatch-sync`](#rain.re-frame/dispatch-sync) - Dispatch a Re-frame event synchronously.
    -  [`dispatcher`](#rain.re-frame/dispatcher) - Returns an event handler that dispatches a Re-frame event.
    -  [`href-alpha`](#rain.re-frame/href-alpha) - Returns a path for a named route.
    -  [`reg-event-db`](#rain.re-frame/reg-event-db) - Register a Re-frame DB event handler.
    -  [`reg-event-fx`](#rain.re-frame/reg-event-fx) - Register a Re-frame effect event handler.
    -  [`reg-sub`](#rain.re-frame/reg-sub) - Register a Re-frame subscription.
    -  [`set-page`](#rain.re-frame/set-page) - Dispatch the <code>[:rain.re-frame/set-page match]</code> event to change the page when a new match is detected.
    -  [`subscribe`](#rain.re-frame/subscribe) - Returns a Re-frame subscription.
    -  [`wrap-rf`](#rain.re-frame/wrap-rf) - A Ring middleware to add support for Re-frame in server components.

-----
# <a name="rain.biff">rain.biff</a>


Functions for using Rain on top of Biff.

  Core Biff functions are taken verbatim from the current Biff code, except the
  XTDB dependency is removed. This enables using Biff with other databases like
  Postgres or Datomic.




## <a name="rain.biff/use-chime">`use-chime`</a><a name="rain.biff/use-chime"></a>
``` clojure

(use-chime {:keys [biff/features biff/plugins biff.chime/tasks], :as ctx})
```

A Biff component for `chime`. Same as `com.biffweb/use-chime`, but without
  XTDB.
<p><sub><a href="/blob/main/src/rain/biff.clj#L41-L51">Source</a></sub></p>

## <a name="rain.biff/use-jetty">`use-jetty`</a><a name="rain.biff/use-jetty"></a>
``` clojure

(use-jetty {:biff/keys [host port handler], :or {host "localhost", port 8080}, :as ctx})
```

A Biff component for `jetty`. Same as `com.biffweb/use-jetty`, but without
  XTDB.
<p><sub><a href="/blob/main/src/rain/biff.clj#L13-L26">Source</a></sub></p>

## <a name="rain.biff/use-shadow-cljs">`use-shadow-cljs`</a><a name="rain.biff/use-shadow-cljs"></a>
``` clojure

(use-shadow-cljs {:keys [shadow-cljs/mode], :as ctx})
```

A Biff component for `shadow-cljs`.
<p><sub><a href="/blob/main/src/rain/biff.clj#L28-L39">Source</a></sub></p>

-----
# <a name="rain.core">rain.core</a>


Functions to support rendering pages with Reitit and Hiccup.




## <a name="rain.core/bootstrap-data-tag">`bootstrap-data-tag`</a><a name="rain.core/bootstrap-data-tag"></a>
``` clojure

(bootstrap-data-tag {:rain/keys [bootstrap-data], :as _request})
```

Returns a `<script>` tag in Hiccup format that encodes the
  `:rain/bootstrap-data` key from the Ring request as
  `application/transit+json`. If the `:rain/bootstrap-data` key is not found,
  returns `nil`.
<p><sub><a href="/blob/main/src/rain/core.clj#L60-L68">Source</a></sub></p>

## <a name="rain.core/csrf-meta-tag">`csrf-meta-tag`</a><a name="rain.core/csrf-meta-tag"></a>
``` clojure

(csrf-meta-tag request)
```

Returns a `<meta>` tag for a CSRF token in Hiccup format if
  `:anti-forgery-token` is in the request. Otherwise returns `nil`.

  Example:

  ```clojure
  [:meta {:name "csrf-token" :content "..."}]
  ```
<p><sub><a href="/blob/main/src/rain/core.clj#L70-L81">Source</a></sub></p>

## <a name="rain.core/export-pages">`export-pages`</a><a name="rain.core/export-pages"></a>
``` clojure

(export-pages pages dir)
```

Export static pages to a directory.
<p><sub><a href="/blob/main/src/rain/core.clj#L157-L164">Source</a></sub></p>

## <a name="rain.core/main-cljs-bundle-path">`main-cljs-bundle-path`</a><a name="rain.core/main-cljs-bundle-path"></a>
``` clojure

(main-cljs-bundle-path)
```

Returns the path to the main CLJS bundle if the manifest file exists.
  Otherwise returns `nil`.

  Example:

  ```clojure
  ; Dev:
  "/js/main.js"

  ; Prod:
  "/js/main.9528D63C2BDE006EA0667792187CAD3C.js"
  ```
<p><sub><a href="/blob/main/src/rain/core.clj#L25-L41">Source</a></sub></p>

## <a name="rain.core/main-cljs-bundle-tag">`main-cljs-bundle-tag`</a><a name="rain.core/main-cljs-bundle-tag"></a>
``` clojure

(main-cljs-bundle-tag)
```

Returns a `<script>` tag for the main CLJS bundle if the manifest file
  exists. Otherwise returns `nil`.

  Example:

  ```clojure
  ; Dev
  [:script {:src "/js/main.js"}]

  ; Prod
  [:script {:src "/js/main.9528D63C2BDE006EA0667792187CAD3C.js"}]
  ```
<p><sub><a href="/blob/main/src/rain/core.clj#L43-L58">Source</a></sub></p>

## <a name="rain.core/main-stylesheet-path">`main-stylesheet-path`</a><a name="rain.core/main-stylesheet-path"></a>
``` clojure

(main-stylesheet-path)
```

Returns the path of the main stylesheet if the manifest file exists.
  If no manifest file is found, try `public/css/main.css` instead. Otherwise
  returns `nil`.
<p><sub><a href="/blob/main/src/rain/core.clj#L96-L104">Source</a></sub></p>

## <a name="rain.core/main-stylesheet-tag">`main-stylesheet-tag`</a><a name="rain.core/main-stylesheet-tag"></a>
``` clojure

(main-stylesheet-tag)
```

Returns the main stylesheet `<link>` tag if the main CSS file is found.
  Otherwise returns `nil`.
<p><sub><a href="/blob/main/src/rain/core.clj#L106-L111">Source</a></sub></p>

## <a name="rain.core/meta-tags">`meta-tags`</a><a name="rain.core/meta-tags"></a>
``` clojure

(meta-tags request)
```

Returns a list of `<meta>` tags in Hiccup format.

  Included tags:

  - [`rain.core/csrf-meta-tag`](#rain.core/csrf-meta-tag)
<p><sub><a href="/blob/main/src/rain/core.clj#L83-L90">Source</a></sub></p>

## <a name="rain.core/script-tags">`script-tags`</a><a name="rain.core/script-tags"></a>
``` clojure

(script-tags request)
```

Returns list of `<script>` tags in Hiccup format.

  Included tags:

  - [`rain.core/bootstrap-data-tag`](#rain.core/bootstrap-data-tag)
  - [`rain.core/main-cljs-bundle-tag`](#rain.core/main-cljs-bundle-tag)
<p><sub><a href="/blob/main/src/rain/core.clj#L122-L132">Source</a></sub></p>

## <a name="rain.core/site-routes">`site-routes`</a><a name="rain.core/site-routes"></a>
``` clojure

(site-routes routes)
```

Returns site routes wrapped with the [`wrap-page`](#rain.core/wrap-page) middleware. This
  includes all routes except static routes.
<p><sub><a href="/blob/main/src/rain/core.clj#L170-L177">Source</a></sub></p>

## <a name="rain.core/static-pages">`static-pages`</a><a name="rain.core/static-pages"></a>
``` clojure

(static-pages routes & {:as ctx})
```

Return a map of page paths to page HTML, generated from the static routes.

  The `:static-paths` key in the route data can be used to control which paths
  are generated by this function.

  Includes the following route transformations:

  - If a route path ends with a slash, it will be renamed to end with `index.html`.
  - If a route path doesn't have an extension, it will be renamed to end with `.html`.
<p><sub><a href="/blob/main/src/rain/core.clj#L188-L216">Source</a></sub></p>

## <a name="rain.core/stylesheet-tags">`stylesheet-tags`</a><a name="rain.core/stylesheet-tags"></a>
``` clojure

(stylesheet-tags)
```

Returns a list of stylesheet `<link>` tags in Hiccup format.

  Included tags:

  - [`rain.core/main-stylesheet-tag`](#rain.core/main-stylesheet-tag)
<p><sub><a href="/blob/main/src/rain/core.clj#L113-L120">Source</a></sub></p>

## <a name="rain.core/wrap-page">`wrap-page`</a><a name="rain.core/wrap-page"></a>
``` clojure

(wrap-page handler)
```

A Ring middleware that adds support for page config keys in route definitions.

  Included page config keys:

  - **`:server-props`** — Render props for a server-side rendered page.
  - **`:static-props`** — Render props for a static page.
  - **`:static-paths`** — Generate paths to be rendered as static pages.
<p><sub><a href="/blob/main/src/rain/core.clj#L134-L155">Source</a></sub></p>

-----
# <a name="rain.re-frame">rain.re-frame</a>


Functions to support rendering Reagent and Re-frame components on both the
  JVM and browser.




## <a name="rain.re-frame/*app-db*">`*app-db*`</a><a name="rain.re-frame/*app-db*"></a>




A dynamic var for the current Re-frame app DB.

  **Client:**

  Alias of `re-frame.db/app-db`

  **Server:**

  An `IDeref` that always returns `{}`.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L13-L24">Source</a></sub></p>

## <a name="rain.re-frame/atom">`atom`</a><a name="rain.re-frame/atom"></a>
``` clojure

(atom init-val)
```

Returns a Reagent atom.

  **Client:**

  Alias of `reagent.core/atom`.

  **Server:**

  Returns an `IDeref` that always returns the `init-val`.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L207-L219">Source</a></sub></p>

## <a name="rain.re-frame/current-page">`current-page`</a><a name="rain.re-frame/current-page"></a>
``` clojure

(current-page _)
```

A Reagent component to render the current page.

     **Client:**

     Renders the `:get` function in the current route match data as a Reagent
     component. Use `[:rain.re-frame/set-page match]` to set the current route
     match.

     **Server:**

     Not available. On the server, the `:get` function in the route match for
     the request is called directly by [`rain.re-frame/wrap-rf`](#rain.re-frame/wrap-rf) to produce
     Hiccup. This Hiccup is rendered in the `<div id="app">` within the final
     HTML document before sending it to the client.

     In addition, the `:server-props` or `:static-props` from the route match
     data will be serialized into a `<script>` tag using Transit to allow for
     client-side hydration.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L75-L98">Source</a></sub></p>

## <a name="rain.re-frame/dispatch">`dispatch`</a><a name="rain.re-frame/dispatch"></a>
``` clojure

(dispatch event)
```

Dispatch a Re-frame event asynchronously.

  **Client:**

  Alias of `re-frame.core/dispatch`.

  **Server:**

  No-op. Dispatching events is not supported on the server.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L181-L192">Source</a></sub></p>

## <a name="rain.re-frame/dispatch-sync">`dispatch-sync`</a><a name="rain.re-frame/dispatch-sync"></a>
``` clojure

(dispatch-sync event)
```

Dispatch a Re-frame event synchronously.

  **Client:**

  Alias of `re-frame.core/dispatch-sync`.

  **Server:**

  No-op. Dispatching events is not supported on the server.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L194-L205">Source</a></sub></p>

## <a name="rain.re-frame/dispatcher">`dispatcher`</a><a name="rain.re-frame/dispatcher"></a>
``` clojure

(dispatcher event)
```

Returns an event handler that dispatches a Re-frame event.

  **Client:**

  When the event handler function is called, it invokes `re-frame.core/dispatch`
  with the `event`.

  **Server:**

  Returns `nil`. This will cause the handler attribute (e.g. `:on-click`) to be
  omitted from the final HTML, preventing hydration errors.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L163-L179">Source</a></sub></p>

## <a name="rain.re-frame/href-alpha">`href-alpha`</a><a name="rain.re-frame/href-alpha"></a>
``` clojure

(href-alpha router name)
(href-alpha router name path-params)
```

Returns a path for a named route.

  **EXPERIMENTAL**

  In the future, another function will be added to make the `router` argument
  implicit and this function will be removed.

  **Client:**

  Same implementation on client and server.

  **Server:**

  Same implementation on client and server.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L249-L268">Source</a></sub></p>

## <a name="rain.re-frame/reg-event-db">`reg-event-db`</a><a name="rain.re-frame/reg-event-db"></a>
``` clojure

(reg-event-db id handler)
(reg-event-db id interceptors handler)
```

Register a Re-frame DB event handler.

  **Client:**

  Alias of `re-frame.core/reg-event-db`.

  **Server:**

  No-op. Dispatching events is not supported on the server.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L133-L146">Source</a></sub></p>

## <a name="rain.re-frame/reg-event-fx">`reg-event-fx`</a><a name="rain.re-frame/reg-event-fx"></a>
``` clojure

(reg-event-fx id handler)
(reg-event-fx id interceptors handler)
```

Register a Re-frame effect event handler.

  **Client:**

  Alias of `re-frame.core/reg-event-fx`.

  **Server:**

  No-op. Dispatching events is not supported on the server.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L148-L161">Source</a></sub></p>

## <a name="rain.re-frame/reg-sub">`reg-sub`</a><a name="rain.re-frame/reg-sub"></a>
``` clojure

(reg-sub query-id f)
```

Register a Re-frame subscription.

  **Client:**

  Alias of `re-frame.core/reg-sub`.

  **Server:**

  Re-implementation of `re-frame.core/reg-sub`.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L103-L115">Source</a></sub></p>

## <a name="rain.re-frame/set-page">`set-page`</a><a name="rain.re-frame/set-page"></a>
``` clojure

(set-page {:keys [db]} [_ match])
```

Dispatch the `[:rain.re-frame/set-page match]` event to change the page
     when a new match is detected.

     **Client:**

     Returns an effect map with `:db` and `:fx`.

     The `:db` key will contain a new Re-frame DB with updated keys:

     1. Keys from `:server-props` or `:static-props`
     2. `:page`: The current page.
     3. `:match`: The current route match.

     The `:fx` key will contain the effects from the `:fx` key in the current
     page's route match data.

     **Server:**

     Not available.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L36-L63">Source</a></sub></p>

## <a name="rain.re-frame/subscribe">`subscribe`</a><a name="rain.re-frame/subscribe"></a>
``` clojure

(subscribe query)
```

Returns a Re-frame subscription.

  **Client:**

  Alias of `re-frame.core/subscribe`.

  **Server:**

  Re-implementation of `re-frame.core/subscribe`.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L117-L131">Source</a></sub></p>

## <a name="rain.re-frame/wrap-rf">`wrap-rf`</a><a name="rain.re-frame/wrap-rf"></a>
``` clojure

(wrap-rf page-fn)
```

A Ring middleware to add support for Re-frame in server components.

  **Client:**

  Returns the original handler function unmodified since it's unnecessary to
  bind [[`rain.re-frame/*app-db*`](#rain.re-frame/*app-db*)](#rain.re-frame/*app-db*) on the client. Rendering happens later in the
  [`rain.re-frame/current-page`](#rain.re-frame/current-page) component.

  **Server:**

  Returns a wrapped handler function. The wrapped function accepts a map and
  binds the value to [[`rain.re-frame/*app-db*`](#rain.re-frame/*app-db*)](#rain.re-frame/*app-db*). Then the handler function is
  called to render the page to Hiccup.

  _Note:_ To support [Form-2 Reagent components][form2], handler functions returning
  another inner function are accepted.

  [form2]: https://cljdoc.org/d/reagent/reagent/1.2.0/doc/tutorials/creating-reagent-components#form-2--a-function-returning-a-function
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L221-L247">Source</a></sub></p>
