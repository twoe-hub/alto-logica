(ns alto-logica.routes.home
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.walk :as walk]
   [hiccup.page :as page]
   [ring.util.http-response :as response]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [clj-http.client :as client]
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [alto-logica.db.core :refer [query]]
   [alto-logica.middleware :as middleware]
   [alto-logica.html.template :refer [template]]
   ))

(def right-nav
  [:div {:class "dtc v-mid tr pa3"}
   [:a {:class "f6 fw4 grow no-underline white-90 dib ml2 pv2 ph3"
        :href "/auth/login"} "Sign in"]
   [:a {:class "f6 fw4 grow no-underline white-90 dib ml2 pv2 ph3 ba"
        :href "/sign-up"} "Sign up"]])

(def main
  [:div {:class "dt w-100 mw8 center lh-copy"}
   [:h1 {:class "w-40 f1 lh-title"} "A couple of aphorisms about software development these days"]
   [:div {:class "pa4"}
    [:blockquote {:class "athelas ml0 mt0 pl4 black-90 bl bw2"}
     [:p {:class "f5 f4-m f3-l lh-copy measure mt0"}
      "Software-wise I really wish we'd done almost everything differently. The focus has been on eyeballs and engagement, not like \"does it work\" or \"can it resist attack by a bad actor\". Almost no modern software works well or is safe in any serious sense. It's always on fire. A sinking ship with everyone frantically bailing. Swiss cheese. Pick your metaphor. So my dream setup would involve \"software that has any sort of reliability\". Which probably requires rewinding time to before the personal computing era and enacting liability legislation or something."]
     [:cite {:class "f6 tracked fs-normal"} "―Graydon Hoare"]]]

   [:div {:class "pa4"}
    [:blockquote {:class "athelas ml0 mt0 pl4 black-90 bl bw2"}
     [:p {:class "f5 f4-m f3-l lh-copy measure mt0"}
      "The root problem, lying under all of this, is that God clearly intended for the programmer-to-program ratio to be one-to-many. It's much more productive and engaging to work that way. Programs should be small, and when you need a lot of code to solve a large problem, you should create a system and give it the respect that systems deserve. The vision that seems ensconced in the modern Java community is one of Big Programs where the programmer-to-program ratio is many-to-one. I've written at length about why this leads inexorably to political behavior and low productivity."]
     [:cite {:class "f6 tracked fs-normal"} "―Michael O. Church"]]]])

(def template-map {:title "Welcome | alto-logica"
                   :css ["/css/tachyons.min.css"
                         "/css/base.css"
                         "/css/home.css"]
                   :right-nav right-nav
                   :main main})

(defn home [request]
  (-> (template template-map)
      (response/ok)
      (response/content-type "text/html; charset=utf-8")))

(defn home-routes []
  [["/" {:get home}]])
