(ns alto-logica.routes.sign-up
  (:require
   [java-time :refer [local-date]]
   [clojure.pprint :refer [pprint]]
   [clojure.walk :as walk]
   [hiccup.page :as page]
   [ring.util.response :refer [redirect]]
   [ring.util.http-response :as response]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [clj-http.client :as client]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [buddy.hashers :as hashers]
   [alto-logica.company.validate :refer [validate]]
   [alto-logica.db.core :refer [query queries]]
   [alto-logica.middleware :as middleware]
   [alto-logica.html.template :refer [template]]))

(def right-nav [:div {:class "dtc v-mid tr pa3"}
                [:span "Already have an account?"
                 [:a {:class "f6 fw4 grow no-underline white-90 dib ml2 pv2 ph3 ba", :href "/auth/login"} "Sign in"]]])

(def template-map {:title "Sign up | alto-logica"
                   :css ["/css/tachyons.min.css"
                         "/css/base.css"
                         "/css/sign_up.css"]
                   :js ["/js/app/cljs_base.js"
                        "/js/app/alto_logica/register/sign_up.js"]
                   :csrf anti-forgery-field
                   :right-nav right-nav
                   :main [:main#content {:class "pa4 black-80"}]})

(defn parse-date [s]
  (if (nil? s)
    nil
    (local-date "dd/MM/yyyy" s)))

(defn create-company! [{:keys [params]}]
  (pprint params)
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (let [params (assoc params :password (hashers/derive (params :password)))
          params (assoc params :incorp-date (parse-date (params :incorp-date)))]
      (pprint params)
      (try
        (query :create-company! params)
        (response/ok {:status :ok})
        (catch Exception e
          (response/internal-server-error
           {:errors {:server-error ["Failed to create account!"]}})))
      )))

(defn form [request]
  (-> (template template-map)
      (response/ok)
      (response/content-type "text/html; charset=utf-8")))

(defn sign-up-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/sign-up" {:get form}]
   ["/sign-up/create" {:post create-company!}]])
