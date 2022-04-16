(ns alto-logica.auth.login
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [reagent.core :as rcore]
            [reagent.dom :as rdom]
            [alto-logica.auth.validate :refer [validate]]))

(defn auth! [e fields errors]
  (.preventDefault e)
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/auth/auth"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "__anti-forgery-token"))}
           :params @fields
           :handler #(do
                       (.replace (.-location js/window) (get-in % [:next]))
                       (reset! fields nil)
                       (reset! errors nil))
           :error-handler #(reset! errors (get-in % [:response :errors]))})))

(defn username-input [fields errors]
  [:div {:class "mt3"}
   [:label {:class "db fw6 lh-copy f6"
            :for "username"} "Email"]
   [:input {:id "username"
            :name :username
            :class "pa2 input-reset ba bg-transparent hover-bg-offw w-100"
            :type "email"
            :aria-describedby "username-error"
            :on-change #(swap! fields assoc :username (-> % .-target .-value))
            :value (:username @fields)}]
   (when-let [error (:username @errors)]
     [:small {:id "username-error", :class "f6 lh-copy brick db mb2"}
      (string/join error)])])

(defn pwd-input [fields errors]
  [:div {:class "mt3"}
   [:label {:class "db fw6 lh-copy f6"
            :for "password"} "Password"]
   [:input {:id "password"
            :name :password
            :class "b pa2 input-reset ba bg-transparent hover-bg-offw w-100"
            :type "password"
            :aria-describedby "password-error"
            :on-change #(swap! fields assoc :password (-> % .-target .-value))
            :value (:password @fields)}]
   (when-let [error (:password @errors)]
     [:small {:id "password-error", :class "f6 lh-copy brick db mb2"}
      (string/join error)])])

(defn login-form []
  (let [fields (rcore/atom {})
        errors (rcore/atom nil)]
    (fn []
      [:form#loginForm.auth-form {:class "measure center" :method "post", :action "#"}
       [:fieldset {:id "fieldset-signin", :class "ba b--transparent ph0 mh0"}
        [:legend {:class "f4 fw6 ph0 mh0"} "Sign In"]
       (username-input fields errors)
       (pwd-input fields errors)
       ]
      [:div
       [:input
        {:class "b ph3 pv2 input-reset ba b--black bg-transparent grow pointer f6 dib"
         :type "submit"
         :on-click #(auth! % fields errors)
         :value "Sign in"}]]])))

(rdom/render [login-form] (gdom/getElement "content"))

(loader/set-loaded! :auth)
