(ns rain.biff
  "Functions for using Rain on top of Biff.

  Core Biff functions are taken verbatim from the current Biff code, except the
  XTDB dependency is removed. This enables using Biff with other databases like
  Postgres or Datomic."
  (:require [clojure.tools.logging :as log]
            [chime.core :as chime]
            [ring.adapter.jetty9 :as jetty]
            [shadow.cljs.devtools.api :as shadow-api]
            [shadow.cljs.devtools.server :as shadow-server]))

(defn use-jetty
  "A Biff component for `jetty`. Same as `com.biffweb/use-jetty`, but without
  XTDB."
  [{:biff/keys [host port handler]
    :or {host "localhost"
         port 8080}
    :as ctx}]
  (let [server (jetty/run-jetty (fn [req] (handler (merge ctx req)))
                                {:host host
                                 :port port
                                 :join? false
                                 :allow-null-path-info true})]
    (log/info "Jetty running on" (str "http://" host ":" port))
    (update ctx :biff/stop conj #(jetty/stop-server server))))

(defn use-shadow-cljs
  "A Biff component for `shadow-cljs`."
  [{:keys [shadow-cljs/mode] :as ctx}]
  (shadow-server/start!)
  (if (= mode :dev)
    (do
      (shadow-api/watch :dev)
      (update ctx :biff/stop conj #(shadow-server/stop!)))
    (do
      (shadow-api/release :prod)
      (shadow-server/stop!)
      ctx)))

(defn use-chime
  "A Biff component for `chime`. Same as `com.biffweb/use-chime`, but without
  XTDB."
  [{:keys [biff/features biff/plugins biff.chime/tasks] :as ctx}]
  (reduce (fn [ctx {:keys [schedule task]}]
            (let [f (fn [_] (task ctx))
                  scheduler (chime/chime-at (schedule) f)]
              (update ctx :biff/stop conj #(.close scheduler))))
          ctx
          (or tasks
              (some->> (or plugins features) deref (mapcat :tasks)))))
