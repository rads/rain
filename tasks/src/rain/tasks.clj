(ns rain.tasks
  (:refer-clojure :exclude [test])
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [rads.xtrace :as xtrace :refer [shell]]))

(defn- parallel [tasks opts]
  (->> tasks
       (map (fn [task] (future (task opts))))
       (map deref)
       dorun)
  (System/exit 0))

(defn postcss [& {:keys [extra-env extra-args]}]
  (apply shell
         {:extra-env (merge {"NODE_ENV" "production"}
                            extra-env)}
         "npx" "postcss"
         "resources/tailwind.css"
         "--config" "resources"
         "-o" "./target/resources/public/css/main.css"
         "--verbose"
         extra-args))

(defn npm [& _]
  (when (and (fs/exists? "package-lock.json")
             (not (fs/exists? "node_modules")))
    (shell "npm ci")))

(defn dev [& {:as opts}]
  (npm)
  (parallel [(fn [_] (shell {:extra-env {"BIFF_ENV" "dev"}}
                            "clj -M:cljs:shadow-cljs:preload:dev"))
             #(postcss (assoc % :extra-env {"NODE_ENV" "dev"}
                                :extra-args ["-w"]))]
            opts))

(defn lint [& _]
  (let [classpath (:out (shell {:out :string :pre-start-fn nil}
                               "clojure -Spath"))
        pre-start-fn xtrace/*pre-start-fn*
        to-lint (concat ["src" "test" "resources"]
                        (map str (fs/glob "." "*.{clj,cljs,cljc,edn}")))]
    (binding [xtrace/*pre-start-fn*
              (fn [{:keys [cmd] :as opts}]
                (let [cmd' (map #(if (= % classpath) "$(clojure -Spath)" %) cmd)
                      opts (assoc opts :cmd cmd')]
                  (pre-start-fn opts)))]
      (shell "mkdir" "-p" ".clj-kondo")
      (shell "clj-kondo" "--lint" classpath "--copy-configs" "--skip-lint")
      (shell "clj-kondo" "--lint" (str/join ":" to-lint)))))

(defn test-clj [& _]
  (shell "clojure -M:test"))

(defn test-cljs [& _]
  (npm)
  (shell "clojure -M:cljs:cljs-test"))

(defn test [& _]
  (test-clj)
  (test-cljs))

(defn uber [& _]
  (shell "clojure -T:build uber"))
