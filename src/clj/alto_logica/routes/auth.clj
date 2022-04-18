(ns alto-logica.routes.auth
  (:require
   [hiccup.page :as page]
   [ring.util.http-response :as response]
   [ring.util.response :refer [redirect]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [buddy.hashers :as hashers]
   [alto-logica.auth.validate :refer [validate]]
   [alto-logica.db.core :refer [query]]
   [alto-logica.middleware :as middleware]
   [alto-logica.html.template :refer [template]]))

(def default-redirect "/profile")

(def trusted-algs #{:bcrypt+sha512})

(def right-nav [:div {:class "dtc v-mid tr pa3"}
                [:span "Don't have an account, yet?"
                 [:a {:class "f6 fw4 grow no-underline white-90 dib ml2 pv2 ph3 ba"
                      :href "/sign-up"} "Sign up"]]])

(def template-map {:title "Login | alto-logica"
                   :css ["/css/tachyons.min.css"
                         "/css/base.css"]
                   :js ["/js/app/cljs_base.js"
                        "/js/app/alto_logica/auth/login.js"]
                   :csrf anti-forgery-field
                   :right-nav right-nav
                   :main [:main#content {:class "pa4 black-80"}]})

(defn- get-user [email]
  (cske/transform-keys csk/->kebab-case-keyword (query :auth! {:email email})))

(defn auth! [request]
  (let [email (get-in request [:params :username])
        plain-pwd (get-in request [:params :password])
        session (:session request)
        {hashed-pwd :password :as user} (get-user email)]
    (if (hashers/check plain-pwd hashed-pwd)
      (let [next-url (get-in session [:next] default-redirect)
            updated-session (assoc session
                                   :identity (dissoc user :password))]
        (-> (response/ok {:status :ok :next next-url})
            (assoc :session (dissoc session :next))
            (assoc :session updated-session)))
      (response/internal-server-error
       {:errors {:server-error ["Incorrect username or password!"]}}))))

(defn login [request]
  (let [next-url (get-in request [:params :next] default-redirect)
        session (:session request)
        upd-sess (assoc session :next next-url)]
    (->
     (response/content-type
      (response/ok
       (template template-map))
      "text/html; charset=utf-8")
     (assoc :session upd-sess))))

(defn logout [request]
  (-> (redirect "/")
      (assoc :session {})))

(defn auth-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/auth/login" {:get login}]
   ["/auth/auth" {:post auth!}]
   ["/logout" {:get logout}]])
