(ns alto-logica.register.sign-up
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [reagent.core :as rcore]
            [reagent.dom :as rdom]
            [alto-logica.sign-up.validate :refer [validate]]))

(defn create-company! [e fields errors]
  (.preventDefault e)
  (reset! errors {})
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/sign-up/create"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "__anti-forgery-token"))}
           :params @fields
           :handler #(do
                       (.replace (.-location js/window) "/auth/login"))
           :error-handler #(do
                             (.log js/console (str %))
                             (reset! errors (get-in % [:response :errors])))})))

(defn text-input [m fields errors]
  (let [error-id (str (m :id) "-error")
        name (keyword (m :id))]
    [:div {:class "mt3"}
     [:label {:class "db fw6 lh-copy f6"
              :for (m :id)} (m :label)]
     [:input {:id (m :id)
              :name name
              :class "pa2 input-reset ba bg-transparent hover-bg-offw w-100"
              :type (m :type)
              :aria-describedby error-id
              :on-change #(swap! fields assoc name (-> % .-target .-value))
              :value (name @fields)}]
     (when-let [error (name @errors)]
       [:small {:id error-id, :class "f6 lh-copy brick db mb2"}
        (string/join error)])]))

(defn signup-form []
  (let [fields (rcore/atom {})
        errors (rcore/atom nil)]
    (fn []
      [:form {:class "measure center" :method "post", :action "#"}
       [:fieldset {:id "fieldset-signin", :class "ba b--transparent ph0 mh0"}
        [:legend {:class "f4 fw6 ph0 mh0"} "Sign Up"]
        (text-input {:label "Legal Name" :id "name" :type "text"} fields errors)
        (text-input {:label "Website" :id "website" :type "text"} fields errors)
        (text-input {:label "Address" :id "address" :type "text"} fields errors)
        (text-input {:label "Phone" :id "phone" :type "text"} fields errors)
        (text-input {:label "Incorp. Date" :id "incorp-date" :type "text"} fields errors)
        (text-input {:label "Email" :id "email" :type "email"} fields errors)
        (text-input {:label "Password" :id "password" :type "password"} fields errors)]
       [:div.pv2.fl-right.ph3
        [:input
         {:class "b ph3 pv2 ba b--black bg-transparent grow pointer f6 dib"
          :type "submit"
          :on-click #(create-company! % fields errors)
          :value "Save"}]]])))

(rdom/render [signup-form] (gdom/getElement "content"))

(loader/set-loaded! :sign-up)
