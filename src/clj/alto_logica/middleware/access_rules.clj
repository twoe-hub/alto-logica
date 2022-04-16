(ns alto-logica.middleware.access-rules
  (:require
   [clojure.java.io :as io]
   [ring.util.http-response :refer [forbidden]]
   [ring.util.response :refer [redirect]]
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.accessrules :refer [wrap-access-rules]]
   [buddy.auth.backends :as backends]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(defn- permit-all [request] true)

(defn- authenticated-access [request]
  (authenticated? (:session request)))

(defn- handle-unauthorized [request metadata]
  (if (authenticated-access request)
    (forbidden "Not authorised")
    (let [current-url (:uri request)]
      (redirect (format "/auth/login?next=%s" current-url)))))

(defn wrap-authr [handler]
  (let [backend (backends/session)]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend)
        (wrap-access-rules {:rules [{:pattern #"^/favicon.ico$"
                                     :handler permit-all}
                                    {:pattern #"^/css/.*"
                                     :handler permit-all}
                                    {:pattern #"^/fonts/.*"
                                     :handler permit-all}
                                    {:pattern #"^/js/.*"
                                     :handler permit-all}
                                    {:pattern #"^/img/.*"
                                     :handler permit-all}
                                    {:pattern #"^/"
                                     :handler permit-all}
                                    {:pattern #"^/sign-up/.*"
                                     :handler permit-all}
                                    {:pattern #"^/sign-up/*"
                                     :handler permit-all}
                                    {:pattern #"^/auth/.*"
                                     :handler permit-all}
                                    {:pattern #"^/profile/.*"
                                     :handler authenticated-access}
                                    {:pattern #"^/profile/*"
                                     :handler authenticated-access
                                     :request-method :post}
                                    {:pattern #"^/.*"
                                     :handler authenticated-access}
                                    ]
                            :on-error handle-unauthorized})))
  )
