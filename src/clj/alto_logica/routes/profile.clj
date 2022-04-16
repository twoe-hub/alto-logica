(ns alto-logica.routes.profile
  (:require
   [clojure.pprint :refer [pprint]]
   [java-time :refer [local-date]]
   [ring.util.response :refer [redirect]]
   [ring.util.http-response :as response]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [hiccup.page :as page]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [conman.core :refer [snip]]
   [buddy.hashers :as hashers]
   [alto-logica.company.validate :refer [validate]]
   [alto-logica.db.core :refer [query queries]]
   [alto-logica.middleware :as middleware]
   [alto-logica.html.template :refer [template]]))

(def right-nav [:div {:class "dtc v-mid tr pa3"}
                [:a {:class "f6 fw4 grow no-underline white-90 dib ml2 pv2 ph3 ba", :href "/logout"} "Logout"]])

(def sub-nav [:div.pv-half
              [:div {:class "bg-black-90 ph3 pv3 pv4-ns ph4-m ph5-l"}
               [:nav {:class "dt w-100 mw8 center"}
                [:span {:class "white dib mr3 bb-underline", :href "#"} "Profile"]
                [:a {:class "link dim white dib mr3", :href "/docs"} "Compliance Docs"]]]])

(def template-map {:title "Profile | alto-logica"
                   :css ["/css/tachyons.min.css"
                         "/css/base.css"
                         "/css/profile.css"
                         "https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@5.15.4/css/fontawesome.min.css"]
                   :js ["/js/app/cljs_base.js"
                        "/js/app/alto_logica/company/profile.js"]
                   :csrf anti-forgery-field
                   :right-nav right-nav
                   :sub-nav sub-nav
                   :main [:main#content {:class "black-80"}]})

(defn parse-date [s]
  (if (nil? s)
    nil
    (local-date "dd/MM/yyyy" s)))

(defn get-com-by-email [email]
  {:profile (cske/transform-keys csk/->kebab-case-keyword
                                         (query :get-company email))})

(defn get-company [request]
  (response/ok
   (get-com-by-email ((request :session) :identity))))

(defn update-company! [{:keys [params] :as request}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (let [email-map ((request :session) :identity)
          params (conj params email-map)
          params (assoc params :incorp-date (parse-date (params :incorp-date)))]
      (try
        (query :update-company! params)
        (response/ok (get-com-by-email email-map))
        (catch Exception e
          (response/internal-server-error
           {:errors {:server-error ["Failed to update company info!"]}}))))))

(defn profile-page [request]
  (response/content-type
   (response/ok
    (template template-map))
   "text/html; charset=utf-8"))

(defn profile-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/profile" {:get profile-page}]
   ["/profile/company" {:get get-company}]
   ["/profile/update" {:post update-company!}]
   ])
