(ns rain.internal.templates
  (:import (java.security SecureRandom)
           (java.util Base64)))

(defn- new-secret [length]
  (let [buffer (byte-array length)]
    (.nextBytes (SecureRandom/getInstanceStrong) buffer)
    (.encodeToString (Base64/getEncoder) buffer)))

(defn hydration-data-fn [data]
  (merge data {:cookie-secret (new-secret 16)
               :jwt-secret (new-secret 32)}))
