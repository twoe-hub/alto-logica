(ns alto-logica.middleware
  (:require
   [alto-logica.env :refer [defaults]]
   [alto-logica.config :refer [env]]
   [alto-logica.layout :refer [error-page]]
   [alto-logica.middleware.access-rules :refer [wrap-authr]]
   [alto-logica.msg.bundle :refer [msg]]
   [clojure.tools.logging :as log]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.flash :refer [wrap-flash]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring-ttl-session.core :refer [ttl-memory-store]]))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title (msg :error.very-bad/title)
                     :message (msg :error.very-bad/msg)})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title (msg :validation/invalid-token)})}))


(defn wrap-formats [handler]
  (let [wrapped (wrap-restful-format
                 handler
                 {:formats [:json-kw :transit-json :transit-msgpack]})]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn wrap-base [handler]
  (->
   ((:middleware defaults) handler)
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)
            ))
      wrap-authr
      wrap-flash
      wrap-session
      wrap-internal-error))
