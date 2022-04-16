(ns alto-logica.routes.docs
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
   [alto-logica.html.template :refer [template]]

   ))

(def right-nav [:div {:class "dtc v-mid tr pa3"}
                [:a {:class "f6 fw4 grow no-underline white-90 dib ml2 pv2 ph3 ba", :href "/logout"} "Logout"]])

(def sub-nav [:div.pv1
              [:div {:class "bg-black-90 ph3 pv3 pv4-ns ph4-m ph5-l"}
               [:nav {:class "dt w-100 mw8 center"}
                [:a {:class "link dim white dib mr3", :href "/profile"} "Profile"]
                [:span {:class "white dib mr3 bb-underline", :href "#"} "Compliance Docs"]]]])

(def template-map {:title "Profile | alto-logica"
                   :css ["/css/tachyons.min.css"
                         "/css/base.css"
                         "/css/docs.css"
                         "https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@5.15.4/css/fontawesome.min.css"]
                   :js ["/js/app/cljs_base.js"
                        "/js/app/alto_logica/company/docs.js"]
                   :csrf anti-forgery-field
                   :right-nav right-nav
                   :sub-nav sub-nav
                   :main [:main#content {:class "pv1 black-80"}]})

(defn get-docs-by-email [email]
  {:docs (cske/transform-keys csk/->kebab-case-keyword
                              (query :get-docs email))})

(defn list-docs [request]
  (response/ok
   (get-docs-by-email ((request :session) :identity))))

(defn update-doc! [{:keys [params] :as request}]
  (if-let [errors (validate params)]
    (response/bad-request {:errors errors})
    (let [email-map ((request :session) :identity)
          params (conj params email-map)]
      (try
        (query :update-company! params)
        (response/ok (get-docs-by-email email-map))
        (catch Exception e
          (response/internal-server-error
           {:errors {:server-error ["Failed to update company info!"]}}))))))

(defn docs-page [request]
  (response/content-type
   (response/ok
    (template template-map))
   "text/html; charset=utf-8"))

(defn docs-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/docs" {:get docs-page}]
   ["/docs/list" {:get list-docs}]
   ["/docs/update" {:post update-doc!}]
   ])
