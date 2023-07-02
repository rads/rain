(ns {{top/ns}}.{{main/ns}}
  (:require [rain.re-frame :as rrf]
            [{{top/ns}}.{{main/ns}}.app :as app]
            [reagent.dom.client :as rdom]
            [reitit.core :as reitit]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.history :as rfh]
            [superstructor.re-frame.fetch-fx]))

(def container (js/document.getElementById "app"))
(defonce react-root (atom nil))
(defonce reitit-router (atom nil))

(defn on-navigate [match]
  (if @react-root
    (rrf/dispatch [::rrf/set-page match])
    (do
      (rrf/dispatch-sync [::rrf/set-page match])
      (reset! react-root (rdom/hydrate-root container [rrf/current-page])))))

(defn ^:export start! [& _]
  (reset! reitit-router
          (rfe/start! (reitit/router (:routes app/plugin))
                      on-navigate
                      {:use-fragment false})))

(defn ^:dev/after-load render []
  (rfh/stop! @reitit-router)
  (start!)
  (rdom/render @react-root [rrf/current-page]))
