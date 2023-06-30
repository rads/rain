(ns rain.core
  "Functions to support rendering pages with Reitit and Hiccup."
  (:require [babashka.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [cognitect.transit :as t]
            [huff.core :as huff]
            [reitit.core :as r]
            [reitit.ring :as ring])
  (:import (java.io ByteArrayOutputStream)))

(defn- ->transit-str [data]
  (let [baos (ByteArrayOutputStream.)
        writer (t/writer baos :json)]
    (t/write writer data)
    (.toString baos "UTF-8")))

(defn- shadow-cljs-modules []
  (when-let [manifest (io/resource "public/js/manifest.edn")]
    (->> (slurp manifest)
         edn/read-string
         (reduce (fn [r module] (assoc r (:module-id module) module)) {}))))

(defn main-cljs-bundle-path
  "Returns the path to the main CLJS bundle if the manifest file exists.
  Otherwise returns `nil`.

  Example:

  ```clojure
  ; Dev:
  \"/js/main.js\"

  ; Prod:
  \"/js/main.9528D63C2BDE006EA0667792187CAD3C.js\"
  ```"
  []
  (let [{:keys [main]} (shadow-cljs-modules)]
    (when main
      (str "/js/" (:output-name main)))))

(defn main-cljs-bundle-tag
  "Returns a `<script>` tag for the main CLJS bundle if the manifest file
  exists. Otherwise returns `nil`.

  Example:

  ```clojure
  ; Dev
  [:script {:src \"/js/main.js\"}]

  ; Prod
  [:script {:src \"/js/main.9528D63C2BDE006EA0667792187CAD3C.js\"}]
  ```"
  []
  (when-let [src (main-cljs-bundle-path)]
    [:script {:type "text/javascript" :src src :defer true}]))

(defn bootstrap-data-tag
  "Returns a `<script>` tag in Hiccup format that encodes the
  `:rain/bootstrap-data` key from the Ring request as
  `application/transit+json`. If the `:rain/bootstrap-data` key is not found,
  returns `nil`."
  [{:rain/keys [bootstrap-data] :as _request}]
  (when bootstrap-data
    [:script#bootstrap-data {:type "application/transit+json"}
     [:hiccup/raw-html (->transit-str bootstrap-data)]]))

(defn csrf-meta-tag
  "Returns a `<meta>` tag for a CSRF token in Hiccup format if
  `:anti-forgery-token` is in the request. Otherwise returns `nil`.

  Example:

  ```clojure
  [:meta {:name \"csrf-token\" :content \"...\"}]
  ```"
  [request]
  (when (:anti-forgery-token request)
    [:meta {:name "csrf-token" :content (:anti-forgery-token request)}]))

(defn meta-tags
  "Returns a list of `<meta>` tags in Hiccup format.

  Included tags:

  - `rain.core/csrf-meta-tag`"
  [request]
  (list [csrf-meta-tag request]))

(defn- css-manifest []
  (when-let [path (io/resource "public/css/manifest.json")]
    (json/read-str (slurp path) {:key-fn str})))

(defn main-stylesheet-path
  "Returns the path of the main stylesheet if the manifest file exists.
  If no manifest file is found, try `public/css/main.css` instead. Otherwise
  returns `nil`."
  []
  (if-let [manifest (css-manifest)]
    (str "/css/" (get manifest "main.css"))
    (when (io/resource "public/css/main.css")
      "/css/main.css")))

(defn main-stylesheet-tag
  "Returns the main stylesheet `<link>` tag if the main CSS file is found.
  Otherwise returns `nil`."
  []
  (when-let [href (main-stylesheet-path)]
    [:link {:rel "stylesheet" :href href}]))

(defn stylesheet-tags
  "Returns a list of stylesheet `<link>` tags in Hiccup format.

  Included tags:

  - `rain.core/main-stylesheet-tag`"
  []
  (list [main-stylesheet-tag]))

(defn script-tags
  "Returns list of `<script>` tags in Hiccup format.

  Included tags:

  - `rain.core/bootstrap-data-tag`
  - `rain.core/main-cljs-bundle-tag`"
  [request]
  (list
    [bootstrap-data-tag request]
    [main-cljs-bundle-tag]))

(defn wrap-page
  "A Ring middleware that adds support for page config keys in route definitions.

  Included page config keys:

  - **`:server-props`** — Render props for a server-side rendered page.
  - **`:static-props`** — Render props for a static page.
  - **`:static-paths`** — Generate paths to be rendered as static pages."
  [handler]
  (fn [request]
    (let [default-layout (or (:rain/layout request) (fn [_ content] content))
          render (or (:rain/render request) huff/html)
          match (ring/get-match request)
          {:keys [server-props static-props layout]
           :or {layout default-layout}} (:data match)
          get-props (or server-props static-props (constantly nil))
          props (get-props request)
          request' (assoc request :rain/bootstrap-data {:props props})
          body (->> props handler (layout request') render)]
      {:status 200
       :headers {"content-type" "text/html"}
       :body body})))

(defn export-pages
  "Export static pages to a directory."
  [pages dir]
  (doseq [[path content] pages
          :let [full-path (cond-> (str dir path)
                                  (str/ends-with? path "/") (str "index.html"))]]
    (io/make-parents full-path)
    (spit full-path content)))

(defn- static-route? [route]
  (let [[_ {:keys [static-props static-paths]}] route]
    (or static-props static-paths)))

(defn site-routes
  "Returns site routes wrapped with the `wrap-page` middleware. This
  includes all routes except static routes."
  [routes]
  (r/routes
    (r/router
      (remove static-route? (r/routes (r/router routes)))
      {:data {:middleware [wrap-page]}})))

(defn- static-routes
  "Returns static routes wrapped with the `wrap-page` middleware. This only
  includes static routes -- use `site-routes` to get the other routes."
  [routes]
  (r/routes
    (r/router
      (filter static-route? (r/routes (r/router routes)))
      {:data {:middleware [wrap-page]}})))

(defn static-pages
  "Return a map of page paths to page HTML, generated from the static routes.

  The `:static-paths` key in the route data can be used to control which paths
  are generated by this function.

  Includes the following route transformations:

  - If a route path ends with a slash, it will be renamed to end with `index.html`.
  - If a route path doesn't have an extension, it will be renamed to end with `.html`."
  [routes & {:as ctx}]
  (let [routes (static-routes routes)
        router (ring/router routes)
        handler (ring/ring-handler router)]
    (apply merge
           (for [n (set (keep (fn [[_ {:keys [name]}]] name) routes))
                 :let [match #(apply r/match-by-name router n %&)
                       static-paths (:static-paths (:data (match)))]]
             (->> (if static-paths (static-paths) [{}])
                  (map (fn [d]
                         (let [m (match d)
                               request (merge ctx {:request-method :get
                                                   :uri (:path m)
                                                   ::r/match m})]
                           [(:path m) request])))
                  (map (fn [[p c]] [(if (str/ends-with? p "/") (str p "index") p) c]))
                  (map (fn [[p c]] [(str p ".html") c]))
                  (map (fn [[p c]] [p (:body (handler c))]))
                  (into {}))))))
