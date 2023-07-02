(ns {{top/ns}}.{{main/ns}}.app
  (:require [rain.re-frame :as rrf]
            [reitit.core :as r]))

(declare href)

(defn index [_]
  [:div.mx-auto.max-w-4xl.p-12.prose
   [:h1.text-center.font-semibold
    "Welcome to Rain!"]
   [:div.w-full.text-center.text-xl
    "The code for this page is in "
    [:code "src/{{top/file}}/{{main/file}}/app.cljc"]
    "."]
   [:h2.text-center
    "What's next?"]
   [:ul
    [:li
     "Try out hot-reloading by changing the " [:code "index"] " function."]
    [:li
     "Use " [:a {:href "#"} "Tailwind CSS"]
     " classes to style your Hiccup components."]
    [:li
     "Run " [:code "bb full-build"] " to export your static files to "
     [:code "target/resources/public"] "."]
    [:li
     "Deploy the static files using any JAMstack host."]]])

(def routes
  ["" {:middleware [rrf/wrap-rf]}
   ["/"
    {:name ::index
     :get index
     :static-props (fn [_] {})}]])

(def router (r/router routes))
(def href #(apply rrf/href-alpha router %&))

(def plugin
  {:routes routes})
