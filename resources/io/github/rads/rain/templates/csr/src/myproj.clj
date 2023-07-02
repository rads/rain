(ns {{top/ns}}.{{main/ns}}
  (:require [babashka.fs :as fs]
            [com.biffweb :as biff]
            [rain.core :as rain]
            [rain.biff :as rain-biff]
            [{{top/ns}}.{{main/ns}}.app :as app]
            [{{top/ns}}.{{main/ns}}.ui :as ui]
            [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :as tn-repl]
            [nrepl.cmdline :as nrepl-cmd]))

(defn generate-assets! [{:keys [biff/plugins] :as ctx}]
  (let [routes (keep :routes @plugins)
        src "resources/public"
        dest "target/resources/public"]
    (when (seq (fs/modified-since dest src))
      (fs/copy-tree src dest {:replace-existing true}))
    (rain/export-pages (rain/static-pages routes ctx) dest)
    (biff/delete-old-files {:dir dest :exts [".html"]})))

(def plugins
  [app/plugin])

(def handler
  (-> (constantly {:status 404})
      biff/wrap-base-defaults))

(defn on-save [ctx]
  (biff/add-libs)
  (biff/eval-files! ctx)
  (generate-assets! ctx))

(def initial-system
  {:biff/plugins #'plugins
   :biff.beholder/on-save #'on-save
   :biff/handler #'handler
   :rain/layout #'ui/layout})

(defonce system (atom {}))

(def components
  [biff/use-config
   biff/use-beholder
   rain-biff/use-shadow-cljs
   rain-biff/use-jetty])

(defn start []
  (let [new-system (reduce (fn [system component]
                             (log/info "starting:" (str component))
                             (component system))
                           initial-system
                           components)]
    (reset! system new-system)
    (generate-assets! new-system)))

(defn -main [& args]
  (start)
  (apply nrepl-cmd/-main args))

(defn full-build [_]
  (generate-assets! initial-system))

(defn refresh []
  (doseq [f (:biff/stop @system)]
    (log/info "stopping:" (str f))
    (f))
  (tn-repl/refresh :after `start))

(comment
  ;; Evaluate this if you make a change to initial-system, components, :tasks,
  ;; :queues, or config.edn. If you update secrets.env, you'll need to restart
  ;; the app.
  (refresh)

  ;; If that messes up your editor's REPL integration, you may need to use this
  ;; instead:
  (biff/fix-print (refresh)))
