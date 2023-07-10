(ns rain.re-frame
  "Functions to support rendering Reagent and Re-frame components on both the
  JVM and browser."
  (:refer-clojure :exclude [atom])
  (:require [reitit.core :as reitit]
            #?@(:clj [[huff.core :as huff]]
                :cljs [[cognitect.transit :as t]
                       [goog.functions :refer [debounce]]
                       [goog.events :as gevents]
                       [re-frame.core :as rf]
                       [re-frame.db :as rf-db]
                       [reagent.core :as r]
                       [reitit.frontend.history :as rfh]
                       ["react" :refer [useEffect useState]]]))
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

(defn read-transit [s]
  #?(:cljs
     (t/read transit-reader s)))

#?(:cljs
   (def ^:private bootstrap-data
     (delay
       (some->> (js/document.getElementById "bootstrap-data")
                (.-innerHTML)
                read-transit))))

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
     (let [db' (merge db
                      (if (:match db)
                        {:first-load false}
                        (merge (:props @bootstrap-data) {:first-load true}))
                      {:match match})
           fx (-> match :data :fx seq)]
       (merge {:db db'} (when fx {:fx fx})))))

#?(:cljs
   (rf/reg-event-fx ::set-page set-page))

#?(:cljs (rf/reg-sub ::match (fn [{:keys [match]}] match)))

#?(:cljs
   (defn current-page
     "A Reagent component to render the current page.

     **Client:**

     Renders the `:get` function in the current route match data as a Reagent
     component. Use `[:rain.re-frame/set-page match]` to set the current route
     match.

     **Server:**

     Not available. On the server, the `:get` function in the route match for
     the request is called directly by `rain.re-frame/wrap-rf` to produce
     Hiccup. This Hiccup is rendered in the `<div id=\"app\">` within the final
     HTML document before sending it to the client.

     In addition, the `:server-props` or `:static-props` from the route match
     data will be serialized into a `<script>` tag using Transit to allow for
     client-side hydration."
     [_]
     (when-let [match @(rf/subscribe [::match])]
       (let [page (-> match :data :get)]
         [page match]))))

#?(:clj
   (def ^:private subscriptions (clojure.core/atom {})))

(defn reg-sub
  "Register a Re-frame subscription.

  **Client:**

  Alias of `re-frame.core/reg-sub`.

  **Server:**

  Re-implementation of `re-frame.core/reg-sub`."
  [query-id f]
  #?(:clj (swap! subscriptions assoc query-id f)
     :cljs (rf/reg-sub query-id f)))

(defn subscribe
  "Returns a Re-frame subscription.

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
         [:hiccup/raw-html (huff/html [page-fn props])]))

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

#?(:cljs
   (defn add-scroll-listener! [router-atom]
     (js/document.addEventListener
       "scroll"
       (debounce (fn [_]
                   (.replaceState js/window.history
                                  #js{:scrollX js/window.scrollX
                                      :scrollY js/window.scrollY}
                                  ""
                                  (rfh/-get-path @router-atom)))
                 100)
       true)))

#?(:cljs
   (rf/reg-fx
     ::scroll-to
     (fn [{:keys [x y]}]
       (.scrollTo js/window x y))))

#?(:cljs
   (rf/reg-fx
     ::scroll-into-view
     (fn [fragment]
       (js/setTimeout
         (fn []
           (.scrollIntoView (js/document.getElementById fragment)))
         1))))

#?(:cljs
   (rf/reg-event-fx
     ::restore-scroll-position
     (fn [{:keys [db]} _]
       (let [{:keys [match]} db
             {:keys [event fragment]} match]
         (println match event fragment)
         (when event
           (if (and (= gevents/EventType.POPSTATE (.-type event))
                    (.-state event))
             (do
               (println "scroll-to state")
               {:fx [[::scroll-to {:x (.-scrollX (.-state event))
                                   :y (.-scrollY (.-state event))}]]})
             (if fragment
               (do
                 (println "scroll into view")
                 {:fx [[::scroll-into-view fragment]]})
               (do
                 (println "scroll-to top")
                 {:fx [[::scroll-to {:x 0 :y 0}]]}))))))))
