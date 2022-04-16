(ns alto-logica.common.util
  (:require [tick.alpha.api :as t]
            [tick.locale-en-us]))

(defn parse-date-time [s]
  (if (nil? s)
    nil
    (->> s
         (.toISOString)
         (t/parse)
         (t/zoned-date-time))))

(defn format-date [dt]
  (if-not (nil? dt)
    (t/format (tick.format/formatter "dd/MM/yyyy") dt)
    nil))

(defn format-date-time [dt]
  (if-not (nil? dt)
    (t/format (tick.format/formatter "dd/MM/yyyy HH:mm:ss") dt)
    nil))
