(ns alto-logica.html.template
  (:require
   [hiccup.page :as page]))

(defn header [right-nav]
  "f should be a function of no arguments, to provide right nav contents"
  [:header
   [:div {:class "cover bg-left bg-center-l"
          :style "background-image: url(https://live.staticflickr.com/65535/51035445128_3e04dc7370_k.jpg)"}
    [:div {:class "pb5 pb6-m pb7-l"}
     [:nav {:class "dt w-100 mw8 center"}
      [:div {:class "dtc w3 v-mid pa3"}
       [:a {:class "no-underline dib w2 h3 pa1 ba black" :href "/"}
        [:h1 "alto-logica"]]]
      right-nav]]]])

(defn template [m]
  (page/html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title (:title m)]
    (for [e (:css m)] (page/include-css e))]
   [:body {:class "sans-serif"}
    (header (m :right-nav))
    (when-let [csrf (m :csrf)] (csrf))
    (m :sub-nav)
    (m :main)
    [:footer {:class "pt16 ph3 ph5-m ph6-l mid-gray"}
     [:small {:class "f6 db tc"} "© 2022 "
      [:b "Alto Logica Inc"]"., All Rights Reserved"]]
    (for [e (m :js)] (page/include-js e))]))
