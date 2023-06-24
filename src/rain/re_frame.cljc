(ns rain.re-frame
  (:refer-clojure :exclude [atom])
  (:require [reitit.core :as reitit]
            #?@(:cljs [[cognitect.transit :as t]
                       [re-frame.core :as rf]
                       [re-frame.db :as rf-db]
                       [reagent.core :as r]]))
  #?(:clj (:import (clojure.lang IDeref)))
  #?(:cljs (:require-macros [rain.re-frame])))

(def ^:dynamic *app-db*
  #?(:clj (reify IDeref (deref [_] {}))
     :cljs rf-db/app-db))

#?(:cljs (def ^:private transit-reader (t/reader :json)))

#?(:cljs
   (def ^:private bootstrap-data
     (delay
       (some->> (js/document.getElementById "bootstrap-data")
                (.-innerHTML)
                (t/read transit-reader)))))

#?(:cljs
   (defn set-page [{:keys [db]} [_ match]]
     (let [page (-> match :data :get)
           db' (merge db
                      (when-not (:page db) (:props @bootstrap-data))
                      {:page page :match match})
           fx (-> match :data :fx seq)]
       (merge {:db db'
               :fx fx}))))

#?(:cljs
   (rf/reg-event-fx ::set-page set-page))

#?(:cljs
   (rf/reg-sub
     ::page
     (fn [{:keys [page]}]
       page)))

#?(:cljs
   (defn current-page [_]
     (let [page (rf/subscribe [::page])]
       (fn [props]
         (when @page
           [@page props])))))

#?(:clj
   (def ^:private subscriptions (clojure.core/atom {})))

(defn reg-sub [query-id f]
  #?(:clj (swap! subscriptions assoc query-id f)
     :cljs (rf/reg-sub query-id f)))

(defn subscribe [query]
  #?(:clj (delay
            (when-let [sub-fn (get @subscriptions (first query))]
              (sub-fn @*app-db* query)))
     :cljs (rf/subscribe query)))

(defn reg-event-db
  ([id handler]
   #?(:clj nil :cljs (rf/reg-event-db id handler)))
  ([id interceptors handler]
   #?(:clj nil :cljs (rf/reg-event-db id interceptors handler))))

(defn reg-event-fx
  ([id handler]
   #?(:clj nil :cljs (rf/reg-event-fx id handler)))
  ([id interceptors handler]
   #?(:clj nil :cljs (rf/reg-event-fx id interceptors handler))))

(defn dispatcher [event]
  #?(:cljs
     (fn [e]
       (.preventDefault e)
       (rf/dispatch event))))

(defn dispatch [event]
  #?(:cljs (rf/dispatch event)))

(defn dispatch-sync [event]
  #?(:cljs (rf/dispatch-sync event)))

(defn atom [init-val]
  #?(:clj (delay init-val)
     :cljs (r/atom init-val)))

#_{:clj-kondo/ignore #?(:clj [:unused-binding] :cljs [])}
(defn ^:no-doc -event [f]
  #?(:clj nil :cljs f))

#?(:clj
   (defmacro event
     [& body]
     `(-event (fn ~@body))))

(defn wrap-rf [page-fn]
  #?(:clj
     (fn [props]
       (binding [*app-db* (reify IDeref (deref [_] props))]
         (page-fn props)))

     :cljs page-fn))

#_{:clj-kondo/ignore #?(:clj [:unused-binding] :cljs [])}
(defn ^:no-doc -f
  [func]
  #?(:clj (func) :cljs func))

#?(:clj
   (defmacro f [& body]
     `(-f (fn ~@body))))

(defn href
  ([router name] (href router name nil))
  ([router name path-params]
   (-> router
       (reitit/match-by-name name path-params)
       reitit/match->path)))
