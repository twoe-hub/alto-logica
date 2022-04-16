(ns alto-logica.company.docs
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [goog.dom :as gdom]
            [reagent.dom :as rdom]
            [reagent.core :as rcore]
            [alto-logica.common.util :as util]
            [alto-logica.company.validate :refer [validate]]
            ))

(defonce view-ui-id "view-widget")
(defonce edit-ui-id "edit-widget")
(defonce docs (rcore/atom {}))
(defonce fields (rcore/atom {}))
(defonce errors (rcore/atom {}))

(defn toggle-edit []
  (let [vw (.getElementById js/document view-ui-id)
        ew (.getElementById js/document edit-ui-id)]
    (if (.contains (.-classList ew) "hide")
      (do (.add (.-classList vw) "hide")
          (.remove (.-classList ew) "hide"))
      (do (reset! fields @docs)
          (reset! errors {})
          (.add (.-classList ew) "hide")
          (.remove (.-classList vw) "hide")))))

(defn get-docs []
  (GET "/docs/list"
       {:headers {"Accept" "application/transit+json"}
        :handler #(let [m (:docs %)]
                    (.log js/console m)
                    (reset! fields m)
                    (reset! docs m))}))

(defn update-doc! [e]
  (.preventDefault e)
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/docs/update"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "__anti-forgery-token"))}
           :params @fields
           :handler #(do
                       (swap! docs (fn[n] (conj n @fields)))
                       (toggle-edit))
           :error-handler #(do
                             (.log js/console (str %))
                             (reset! errors (get-in % [:response :errors])))})))

(defn text-input [m]
  (let [error-id (str (m :id) "-error")
        name (keyword (m :id))]
    [:div {:class "mt3"}
     [:label {:class "db fw6 lh-copy f6"
              :for (m :id)} (m :label)]
     [:input {:id (m :id)
              :name name
              :class "pa2 ba bg-transparent hover-bg-black w-100"
              :type (m :type)
              :aria-describedby error-id
              :on-change #(swap! fields assoc name (-> % .-target .-value))
              :value (name @fields)}]
     ;; (when-let [error (name @errors)])
     [:small {:id error-id, :class "f6 lh-copy brick db mb2"}
      (string/join (name @errors))]]))

(defn text-label [m]
  (let [name (keyword (m :id))]
    [:div {:class "mt3"}
     [:label {:class "db fw6 lh-copy f6"} (m :label)]
     [:label {:class "db lh-copy f6"} (@docs name)]]))

;; (defn view-ui []
;;   [:fieldset#view-widget {:class "ba b--transparent ph0 mh0 mt4 measure center"}
;;    [:legend.fl-right.pointer
;;     {:on-click #(toggle-edit)}
;;     [:i.fa.fa-edit.f4]]
;;    (text-label {:label "Email" :id "email"})
;;    (text-label {:label "Legal Name" :id "name"})
;;    (text-label {:label "Phone" :id "phone"})])

(defn edit-ui []
  [:form#edit-widget {:class "measure center hide" :method "post", :action "#"}
   ;; (text-input {:label "Email" :id "email" :type "email"})
   (text-input {:label "Legal Name" :id "name" :type "text"})
   (text-input {:label "Phone" :id "phone" :type "text"})
   (text-input {:label "Address" :id "address" :type "text"})
   (text-input {:label "Website" :id "website" :type "text"})
   (text-input {:label "Incorp. Date" :id "incorp-date" :type "text"})
   [:div.pv2
    [:div.pv2.fl-right.ph3
     [:input
      {:class "b ph3 pv2 ba b--black bg-transparent grow pointer f6 dib"
       :type "submit"
       :on-click #(update-doc! %)
       :value "Save"}]]
    [:div.pv2.fl-right.ph3
     [:input
      {:class "b ph3 pv2 ba b--black bg-transparent grow pointer f6 dib"
       :type "button"
       :on-click #(toggle-edit)
       :value "Cancel"}]]]])

(defn docs-page []
  (get-docs)
  (fn []
    [:div
     ;; (view-ui)
     (edit-ui)
     ]))

(rdom/render [docs-page] (gdom/getElement "content"))

(loader/set-loaded! :docs)
