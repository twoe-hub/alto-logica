(ns alto-logica.company.docs
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [camel-snake-kebab.core :as csk]
            [goog.dom :as gdom]
            [reagent.dom :as rdom]
            [reagent.core :as rcore]
            [alto-logica.common.util :as util]
            [alto-logica.company.validate :refer [validate]]
            ))

(defonce edit-ui-id "edit-widget")
(defonce docs (rcore/atom {}))
(defonce fields (rcore/atom {}))
(defonce errors (rcore/atom {}))
(defonce status-comp [:div
                      [:i {:class "fa fa-spinner fa-spin fa-pulse"}]
                      " Verifying your identity. Do not leave this page, resubmit, or hit the back button."])

(defn set-upload-indicator [name]
  (let [m {(keyword name) status-comp}]
    (reset! errors m)))

(defn get-docs []
  (GET "/docs/list"
       {:headers {"Accept" "application/transit+json"}
        :handler #(let [m (:docs %)]
                    ;; (.log js/console m)
                    (reset! fields m)
                    (reset! docs m))}))

(defn update-doc! [e]
  (let [el (or (.-srcElement e) (.-target e))
        name (.-name el)
        file (aget (.-files el) 0)
        form-data (doto (js/FormData.) (.append name file))]
    (POST "/docs/update"
          {:body form-data
           :response-format :json
           :keywords? true
           :handler #(let [m (:docs %)]
                       (reset! errors {})
                       (reset! fields m)
                       (reset! docs m))
           :error-handler #(.log js/conQsole (str %))})
    (set-upload-indicator name)))

(defn edit-ui []
  [:form {:class "measure center" :method "post"
          ;; :enc-type "multipart/form-data"
          }
   (doall
    (for [{:keys [id name filename url]} @docs]
      (let [id (csk/->kebab-case id)
            error-id (str id "-error")]
        ^{:key id}
        [:div {:class "mt3"}
         [:label {:class "db fw6 lh-copy f6 pv2"} name]
         [:a.pr2.pv2.f6.black-70.dib.grow {:href url }
          [:i.fa-solid.fa-file.pr1] filename]
         [:label.bt-fu.lh-copy.f6 {:for id} "Browse.."]
         [:input {:id id
                  :name id
                  :type "file"
                  :on-change #(update-doc! %)
                  :aria-describedby error-id}]
         [:small {:id error-id, :class "pt2 f6 fw6 brick lh-copy db mb2"}
          ((keyword id) @errors)]])))])

(defn docs-page []
  (get-docs)
  (fn []
    [:div.mt4
     (edit-ui)]))

(rdom/render [docs-page] (gdom/getElement "content"))

(loader/set-loaded! :docs)
