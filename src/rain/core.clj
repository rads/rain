(ns rain.core
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

(defn- get-js-modules []
  (when-let [manifest (io/resource "public/js/manifest.edn")]
    (->> (slurp manifest)
         edn/read-string
         (reduce (fn [r module] (assoc r (:module-id module) module)) {}))))

(defn- cljs-script []
  (let [{:keys [main]} (get-js-modules)]
    (when main
      [:script {:type "text/javascript" :src (str "/js/" (:output-name main))
                :defer true}])))

(defn- bootstrap-data-script [{:rain/keys [bootstrap-data] :as _ctx}]
  (when bootstrap-data
    [:script#bootstrap-data {:type "application/transit+json"}
     [:hiccup/raw-html (->transit-str bootstrap-data)]]))

(defn- csrf-meta-tag [ctx]
  (when (:anti-forgery-token ctx)
    [:meta {:name "csrf-token" :content (:anti-forgery-token ctx)}]))

(defn meta-tags [ctx]
  [csrf-meta-tag ctx])

(defn- css-path [_]
  (if-let [path (io/resource "public/css/manifest.json")]
    (let [manifest (json/read-str (slurp path) {:key-fn str})
          main (get manifest "main.css")]
      (str "/css/" main))
    "/css/main.css"))

(defn stylesheet-tags [ctx]
  [:link {:rel "stylesheet" :href (css-path ctx)}])

(defn script-tags [ctx]
  (list
    [bootstrap-data-script ctx]
    [cljs-script]))

(defn wrap-page [handler]
  (fn [req]
    (let [default-layout (or (:rain/layout req) (fn [_ content] content))
          render (or (:rain/render req) huff/html)
          match (ring/get-match req)
          {:keys [server-props static-props layout]
           :or {layout default-layout}} (:data match)
          props ((or server-props static-props) req)
          req' (assoc req :rain/bootstrap-data {:props props})
          body (->> props handler (layout req') render)]
      {:status 200
       :headers {"content-type" "text/html"}
       :body body})))

(defn export-pages
  [pages dir]
  (doseq [[path content] pages
          :let [full-path (cond-> (str dir path)
                                  (str/ends-with? path "/") (str "index.html"))]]
    (io/make-parents full-path)
    (spit full-path content)))

(defn site-routes [routes]
  (r/routes
    (r/router (remove (fn [[_ {:keys [static-props]}]] static-props) routes)
              {:data {:middleware [wrap-page]}})))

(defn- static-routes [routes]
  (let [base (->> (r/routes (r/router routes))
                  (filter (fn [[_ {:keys [static-props]}]] static-props))
                  (map (fn [[p c]] [(if (str/ends-with? p "/") (str p "index.html") p) c])))]
    (r/routes
      (r/router
        (concat base
                (map (fn [[p c]] [(str/replace p ".html" ".json")
                                  (dissoc c :name)])
                     base))
        {:data {:middleware [wrap-page]}}))))

(defn static-pages [routes & {:as ctx}]
  (let [routes (static-routes routes)
        router (ring/router routes)
        handler (ring/ring-handler router)]
    (apply merge
           (for [n (set (keep (fn [[_ {:keys [name]}]] name) routes))
                 :let [match #(apply r/match-by-name router n %&)]]
             (->> ((:static-paths (:data (match))))
                  (map (fn [d]
                         (let [m (match d)
                               req (merge ctx {:request-method :get
                                               :uri (:path m)})]
                           [(:path m) (:body (handler req))])))
                  (into {}))))))
