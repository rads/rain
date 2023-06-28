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
  "A dynamic var for the current Re-frame app DB.

  **Client:**

  Alias of `re-frame.db/app-db`

  **Server:**

  An `IDeref` that always returns `{}`."
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
   (defn set-page
     "Dispatch the `[:rain.re-frame/set-page match]` event to change the page
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

     Not available."
     [{:keys [db]} [_ match]]
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
   (defn current-page
     "A Reagent component to render the current page."
     [_]
     (let [page (rf/subscribe [::page])]
       (fn [props]
         (when @page
           [@page props])))))

#?(:clj
   (def ^:private subscriptions (clojure.core/atom {})))

(defn reg-sub
  "Register a Re-frame subscription.

  **Client**

  Alias of `re-frame.core/reg-sub`.

  **Server**

  Re-implementation of `re-frame.core/reg-sub`."
  [query-id f]
  #?(:clj (swap! subscriptions assoc query-id f)
     :cljs (rf/reg-sub query-id f)))

(defn subscribe
  "Return a Re-frame subscription.

  **Client:**

  Alias of `re-frame.core/subscribe`.

  **Server:**

  Re-implementation of `re-frame.core/subscribe`."
  [query]
  #?(:clj (delay
            (when-let [sub-fn (get @subscriptions (first query))]
              (sub-fn @*app-db* query)))
     :cljs (rf/subscribe query)))

(defn reg-event-db
  "Register a Re-frame DB event handler.

  **Client:**

  Alias of `re-frame.core/reg-event-db`.

  **Server:**

  No-op. Dispatching events is not supported on the server."
  ([id handler]
   #?(:clj nil :cljs (rf/reg-event-db id handler)))
  ([id interceptors handler]
   #?(:clj nil :cljs (rf/reg-event-db id interceptors handler))))

(defn reg-event-fx
  "Register a Re-frame effect event handler.

  **Client:**

  Alias of `re-frame.core/reg-event-fx`.

  **Server:**

  No-op. Dispatching events is not supported on the server."
  ([id handler]
   #?(:clj nil :cljs (rf/reg-event-fx id handler)))
  ([id interceptors handler]
   #?(:clj nil :cljs (rf/reg-event-fx id interceptors handler))))

(defn dispatcher
  "Returns an event handler that dispatches a Re-frame event.

  **Client:**

  When the event handler function is called, it invokes `re-frame.core/dispatch`
  with the `event`.

  **Server:**

  Returns `nil`. This will cause the handler attribute (e.g. `:on-click`) to be
  omitted from the final HTML, preventing hydration errors."
  [event]
  #?(:cljs
     (fn [e]
       (.preventDefault e)
       (rf/dispatch event))))

(defn dispatch
  "Dispatch a Re-frame event asynchronously.

  **Client:**

  Alias of `re-frame.core/dispatch`.

  **Server:**

  No-op. Dispatching events is not supported on the server."
  [event]
  #?(:cljs (rf/dispatch event)))

(defn dispatch-sync
  "Dispatch a Re-frame event synchronously.

  **Client:**

  Alias of `re-frame.core/dispatch-sync`.

  **Server:**

  No-op. Dispatching events is not supported on the server."
  [event]
  #?(:cljs (rf/dispatch-sync event)))

(defn atom
  "Returns a Reagent atom.

  **Client:**

  Alias of `reagent.core/atom`.

  **Server:**

  Returns an `IDeref` that always returns the `init-val`."
  [init-val]
  #?(:clj (reify IDeref (deref [_] init-val))
     :cljs (r/atom init-val)))

(defn wrap-rf
  "A Ring middleware to add support for Re-frame in server components.

  **Client:**

  Returns the original handler function unmodified since it's unnecessary to
  bind `rain.re-frame/*app-db*` on the client. Rendering happens later in the
  `rain.re-frame/current-page` component.

  **Server:**

  Returns a wrapped handler function. The wrapped function accepts a map and
  binds the value to `rain.re-frame/*app-db*`. Then the handler function is
  called to render the page to Hiccup.

  _Note:_ To support [Form-2 Reagent components][form2], handler functions returning
  another inner function are accepted.

  [form2]: https://cljdoc.org/d/reagent/reagent/1.2.0/doc/tutorials/creating-reagent-components#form-2--a-function-returning-a-function"
  [page-fn]
  #?(:clj
     (fn [props]
       (binding [*app-db* (reify IDeref (deref [_] props))]
         (let [nodes (page-fn props)]
           (if (fn? nodes) (nodes props) nodes))))

     :cljs page-fn))

(defn href-alpha
  "Returns a path for a named route.

  **EXPERIMENTAL**

  In the future, another function will be added to make the `router` argument
  implicit and this function will be removed.

  **Client:**

  Same implementation on client and server.

  **Server:**

  Same implementation on client and server."
  ([router name] (href-alpha router name nil))
  ([router name path-params]
   (-> router
       (reitit/match-by-name name path-params)
       reitit/match->path)))
