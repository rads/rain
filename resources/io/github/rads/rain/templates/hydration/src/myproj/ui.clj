(ns {{top/ns}}.{{main/ns}}.ui
  (:require [rain.core :as rain]
            [clojure.java.io :as io]))

(defn layout [ctx content]
  (list
    [:hiccup/raw-html "<!DOCTYPE html>"]
    [:html {:lang "en"}
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:title "My Project"]
      [rain/meta-tags ctx]
      [rain/stylesheet-tags]]
     [:body
      [:div#app content]
      [rain/script-tags ctx]]]))
