(ns alto-logica.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [alto-logica.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[alto-logica started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[alto-logica has shut down successfully]=-"))
   :middleware wrap-dev})
