(ns alto-logica.company.validate
  (:require
   [struct.core :as st]
   [alto-logica.msg.bundle :refer [msg]]))

(def profile-schema
  [[:name
    [st/required :message (msg :validation/required)]
    st/string {:message (msg :validation/char-limit-exceeded 60)
               :validate (fn [v] (<= (count v) 60))}]
   ;; [:email
   ;;  [st/required :message (msg :validation/required)]
   ;;  [st/email :message (msg :validation/invalid-email)]
   ;;  st/string {:message (msg :validation/char-limit-exceeded 60)
   ;;             :validate (fn [v] (<= (count v) 60))}]
   [:phone
    [st/required :message (msg :validation/required)]
    st/string {:message (msg :validation/char-limit-exceeded 20)
               :validate (fn [v] (<= (count v) 20))}]
   [:address
    [st/required :message (msg :validation/required)]
    st/string {:message (msg :validation/char-limit-exceeded 60)
               :validate (fn [v] (<= (count v) 60))}]
   [:website
    [st/required :message (msg :validation/required)]
    st/string {:message (msg :validation/char-limit-exceeded 60)
               :validate (fn [v] (<= (count v) 60))}]
   [:incorp-date
    [st/required :message (msg :validation/required)]
    st/string {:message (msg :validation/char-limit-exceeded 10)
               :validate (fn [v] (<= (count v) 10))}]
   ])

(defn validate [params]
  (first (st/validate params profile-schema)))
