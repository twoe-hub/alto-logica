(ns alto-logica.routes.docs
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.string :refer [split]]
   [clojure.java.io :as io]
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
   [alto-logica.util.minio-client :refer [get-doc-is put-doc delete-doc]]))

(def right-nav [:div {:class "dtc v-mid tr pa3"}
                [:a {:class "f6 fw4 grow no-underline white-90 dib ml2 pv2 ph3 ba", :href "/logout"} "Logout"]])

(def sub-nav [:div.pv-half
              [:div {:class "bg-black-90 ph3 pv3 pv4-ns ph4-m ph5-l"}
               [:nav {:class "dt w-100 mw8 center"}
                [:a {:class "link dim white dib mr3", :href "/profile"} "Profile"]
                [:span {:class "white dib mr3 bb-underline", :href "#"} "Compliance Docs"]]]])

(def template-map {:title "Company Docs | alto-logica"
                   :css ["/css/tachyons.min.css"
                         "/css/base.css"]
                   :js ["/js/app/cljs_base.js"
                        "/js/app/alto_logica/company/docs.js"
                        "https://kit.fontawesome.com/a96c73f6bb.js"]
                   :right-nav right-nav
                   :sub-nav sub-nav
                   :main [:main#content {:class "pv1 black-80"}]})

(defn- get-docs-by-email [email]
  {:docs (cske/transform-keys csk/->kebab-case-keyword
                              (query :get-docs email))})

(defn list-docs [request]
  (response/ok
   (get-docs-by-email ((request :session) :identity))))

(defn update-doc! [request]
  (let [params (request :params)
        prop (first (keys params))
        {:keys [filename content-type size tempfile]} (prop params)
        uri (str (name prop) "." (last (split filename #"\.")))
        is (io/input-stream tempfile)]
    (put-doc uri is size))
  (response/ok
   (get-docs-by-email ((request :session) :identity))))

(defn docs-page [request]
  (response/content-type
   (response/ok
    (template template-map))
   "text/html; charset=utf-8"))

(defn docs-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/docs" {:get docs-page}]
   ["/docs/list" {:get list-docs}]
   ["/docs/update" {:post update-doc!}]])
