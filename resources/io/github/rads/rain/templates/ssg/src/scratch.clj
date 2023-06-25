(ns {{scratch/ns}}
  "{{description}}")

(defn exec
  "Invoke me with rain exec {{scratch/ns}}/exec"
  [opts]
  (println "exec with" opts))

(defn -main
  "Invoke me with rain main {{scratch/ns}}"
  [& args]
  (println "-main with" args))
