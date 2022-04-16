(ns alto-logica.db.util
  (:require [clojure.string :as string]))

(defn str-regex [s]
  (if (re-matches #"[-@\.\w]+" s)
    (str "'.*" s ".*'")
    "'.*'"))
