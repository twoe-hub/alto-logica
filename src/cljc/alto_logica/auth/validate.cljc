(ns alto-logica.auth.validate
  (:require
   [struct.core :as st]))

(def login-schema
  [[:username
    [st/required :message "Required"]
    st/string]
   [:password
    [st/required :message "Required"]
    st/string]
   ;; [:email
   ;;  [st/required :message (msg :validation/required)]
   ;;  [st/email :message (msg :validation/invalid-email)]
   ;;  st/string {:message (msg :validation/char-limit-exceeded 40)
   ;;             :validate (fn [v] (<= (count v) 40))}]
   ])

(defn validate [params]
  (first (st/validate params login-schema)))
