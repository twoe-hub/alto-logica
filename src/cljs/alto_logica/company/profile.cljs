(ns alto-logica.company.profile
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
(defonce profile (rcore/atom {}))
(defonce fields (rcore/atom {}))
(defonce errors (rcore/atom {}))

(defn toggle-edit []
  (let [vw (.getElementById js/document view-ui-id)
        ew (.getElementById js/document edit-ui-id)]
    (if (.contains (.-classList ew) "hide")
      (do (.add (.-classList vw) "hide")
          (.remove (.-classList ew) "hide"))
      (do (reset! fields @profile)
          (reset! errors {})
          (.add (.-classList ew) "hide")
          (.remove (.-classList vw) "hide")))))

(defn get-company []
  (GET "/profile/company"
       {:headers {"Accept" "application/transit+json"}
        :handler #(let [m (:profile %)
                        m (assoc m :incorp-date (-> (m :incorp-date)
                                                    (util/parse-date-time)
                                                    (util/format-date)))]
                    (.log js/console m)
                    (reset! fields m)
                    (reset! profile m))}))

(defn update-company! [e]
  (.preventDefault e)
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/profile/update"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "__anti-forgery-token"))}
           :params @fields
           :handler #(do
                       (swap! profile (fn[n] (conj n @fields)))
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
              :class "pa2 ba bg-transparent hover-bg-offw w-100"
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
     [:label {:class "db lh-copy f6"} (@profile name)]]))

(defn view-ui []
  [:fieldset#view-widget {:class "ba b--transparent ph0 mh0 mt4 measure center"}
   [:legend.fl-right.pointer
    {:on-click #(toggle-edit)}
    [:i.fa.fa-edit.f4]]
   (text-label {:label "Email" :id "email"})
   (text-label {:label "Legal Name" :id "name"})
   (text-label {:label "Phone" :id "phone"})
   (text-label {:label "Address" :id "address"})
   (text-label {:label "Website" :id "website"})
   (text-label {:label "Incorp. Date (DD/MM/YYYY)" :id "incorp-date"})])

(defn edit-ui []
  [:form#edit-widget {:class "mt4 measure center hide" :method "post", :action "#"}
   (text-input {:label "Legal Name" :id "name" :type "text"})
   (text-input {:label "Phone" :id "phone" :type "text"})
   (text-input {:label "Address" :id "address" :type "text"})
   (text-input {:label "Website" :id "website" :type "text"})
   (text-input {:label "Incorp. Date (DD/MM/YYYY)" :id "incorp-date" :type "text"})
   [:div.pv2
    [:div.pv2.fl-right.ph3
     [:input
      {:class "b ph3 pv2 ba b--black bg-transparent grow pointer f6 dib"
       :type "submit"
       :on-click #(update-company! %)
       :value "Save"}]]
    [:div.pv2.fl-right.ph3
     [:input
      {:class "b ph3 pv2 ba b--black bg-transparent grow pointer f6 dib"
       :type "button"
       :on-click #(toggle-edit)
       :value "Cancel"}]]]])

(defn profile-page []
  (get-company)
  (fn []
    [:div
     (view-ui)
     (edit-ui)
     ]))

(rdom/render [profile-page] (gdom/getElement "content"))

(loader/set-loaded! :profile)
