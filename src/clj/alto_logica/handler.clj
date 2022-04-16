(ns alto-logica.handler
  (:require
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.webjars :refer [wrap-webjars]]
    [mount.core :as mount]
    [reitit.ring :as ring]
    [alto-logica.middleware :as middleware]
    [alto-logica.layout :refer [error-page]]
    [alto-logica.routes.home :refer [home-routes]]
    [alto-logica.routes.sign-up :refer [sign-up-routes]]
    [alto-logica.routes.auth :refer [auth-routes]]
    [alto-logica.routes.profile :refer [profile-routes]]
    [alto-logica.routes.docs :refer [docs-routes]]
    [alto-logica.env :refer [defaults]]
    ))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
     [(home-routes)
      (sign-up-routes)
      (auth-routes)
      (profile-routes)
      (docs-routes)
      ])
    (ring/routes
      (ring/create-resource-handler
        {:path "/"})
      (wrap-content-type
        (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found
         (constantly (error-page {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly (error-page {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn app []
  (middleware/wrap-base #'app-routes))
