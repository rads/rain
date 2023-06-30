(ns ^:no-doc rain.cli
  (:refer-clojure :exclude [test])
  (:require [babashka.cli :as cli]
            [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(defn is-tty
  [fd key]
  (-> ["test" "-t" (str fd)]
      (p/process {key :inherit :env {}})
      deref
      :exit
      (= 0)))

(def tty-out? (memoize #(is-tty 1 :out)))

(defn no-color? [{:keys [color] :as _cli-opts}]
  (or (false? color)
      (not (tty-out?))
      (System/getenv "NO_COLOR")
      (= "dumb" (System/getenv "TERM"))))

(defn magenta [s cli-opts]
  (if (no-color? cli-opts) s (str "\033[35m" s "\033[0m")))

(defn bold [s cli-opts]
  (if (no-color? cli-opts) s (str "\033[1m" s "\033[0m")))

(def help-commands
  (->> [{:command "rain new" :doc "Create a new Clojure/Script project"}
        {:command "rain dev" :doc "Start a dev environment"}
        {:command "rain repl" :doc "Start a Clojure REPL"}
        {:command "rain exec" :doc "Run a function"}
        {:command "rain main" :doc "Run a main namespace"}
        {:command "rain test" :doc "Run all tests"}
        {:command "rain test-clj" :doc "Run CLJ tests"}
        {:command "rain test-cljs" :doc "Run CLJS tests"}
        {:command "rain help" :doc "Display rain help"}]
       (remove nil?)))

(defn prefix [k cli-opts]
  (bold (magenta (format "[rain/%s] " (name k)) cli-opts) cli-opts))

(defn shell* [opts & args]
  (let [args' (if (coll? (first args)) (first args) args)]
    (binding [*out* *err*]
      (println)
      (println (str (prefix :shell opts)
                    (bold (str (str/join " " args')) opts))))
    (apply p/shell opts args')))

(defn exec* [opts & args]
  (let [args' (if (coll? (first args)) (first args) args)]
    (binding [*out* *err*]
      (println)
      (println (str (prefix :shell opts)
                    (bold (str/join " " args') opts))))
    (apply p/exec opts args')))

(defn help [& _]
  (let [max-width (apply max (map #(count (:command %)) help-commands))
        lines (->> help-commands
                   (map (fn [{:keys [command doc]}]
                          (format (str "  %-" (inc max-width) "s %s") command doc))))]
    (println (str "Version: 0.1.x"))
    (println)
    (println (str "Usage: rain <command>\n\n" (str/join "\n" lines)))))

(defn info [text cli-opts]
  (binding [*out* *err*]
    (println (str (prefix :info cli-opts) (bold text cli-opts)))))

(def read-bb-edn
  (memoize #(edn/read-string (slurp "bb.edn"))))

(defn bb-task [sym]
  (get-in (read-bb-edn) [:tasks sym]))

(defn new* [{:keys [opts]}]
  (let [{:keys [template]} opts
        template' (str "io.github.rads/rain.templates." (or template "ssg"))
        git-url (or (:git/url opts) "https://github.com/rads/rain")
        deps-opts (if (:local/root opts)
                    (select-keys opts [:local/root])
                    {:git/url git-url})
        create-opts (-> (merge opts {:template template'} deps-opts)
                        (update-keys #(str "--" (subs (str %) 1))))]
    (apply shell* opts
           "neil" "new" (mapcat identity create-opts))
    (let [proj-dir (name (edn/read-string (:name opts)))]
      (shell* {:dir proj-dir} "git init")
      (shell* {:dir proj-dir} "git add .")
      (shell* {:dir proj-dir} "git commit -m 'First commit'")
      (println)
      (info (str "New project created:") opts)
      (info "" opts)
      (info (str (format "  Template:  %s" template')) opts)
      (info (str (format "  Directory: %s" (fs/absolutize proj-dir))) opts)
      (info (str (format "  Namespace: %s" (str/replace (:name opts) "/" "."))) opts)
      (info "" opts)
      (info (str "Now you can go to your project and start the dev server:") opts)
      (info "" opts)
      (info (str (format "  cd %s && bb dev" proj-dir)) opts)
      (println))))

(defn dev [{:keys [opts]}]
  (if (bb-task 'dev)
    (exec* opts "bb dev")
    (println "Can't find \"dev\" task in bb.edn")))

(defn test [{:keys [opts]}]
  (if (bb-task 'test)
    (exec* opts "bb test")
    (println "Can't find \"test\" task in bb.edn")))

(defn test-clj [{:keys [opts]}]
  (if (bb-task 'test-clj)
    (exec* opts "bb test-clj")
    (println "Can't find \"test-clj\" task in bb.edn")))

(defn test-cljs [{:keys [opts]}]
  (if (bb-task 'test-cljs)
    (exec* opts "bb test-cljs")
    (println "Can't find \"test-cljs\" task in bb.edn")))

(defn repl [{:keys [opts]}]
  (exec* opts "clj"))

(defn main [_]
  (let [main-ns (second *command-line-args*)
        [_ _ & cli-args] (vec *command-line-args*)
        opts (cli/parse-opts cli-args)
        main-ns-str (when main-ns
                      (if (str/starts-with? main-ns ":")
                        main-ns
                        (str " -m " main-ns)))
        cmd-str (str "clojure -M" main-ns-str)]
    (exec* opts cmd-str)))

(defn exec [_]
  (let [fn-sym (second *command-line-args*)
        [_ _ & cli-args] (vec *command-line-args*)
        opts (cli/parse-opts cli-args)
        fn-sym-str (if (str/starts-with? fn-sym ":")
                     fn-sym
                     (str " " fn-sym))
        cmd-str (str "clojure -X" fn-sym-str)]
    (exec* opts cmd-str)))

(def commands
  [{:cmds ["help"] :fn help}
   {:cmds ["new"] :fn new* :args->opts [:name]}
   {:cmds ["dev"] :fn dev}
   {:cmds ["test"] :fn test}
   {:cmds ["test-clj"] :fn test-clj}
   {:cmds ["test-cljs"] :fn test-cljs}
   {:cmds ["repl"] :fn repl}
   {:cmds ["main"] :fn main}
   {:cmds ["exec"] :fn exec}
   {:cmds [] :fn help}])

(defn rain [main-args]
  (cli/dispatch commands main-args {}))

(defn -main [& args]
  (rain args))
