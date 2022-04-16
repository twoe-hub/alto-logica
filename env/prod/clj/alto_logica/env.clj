(ns alto-logica.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[alto-logica started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[alto-logica has shut down successfully]=-"))
   :middleware identity})
