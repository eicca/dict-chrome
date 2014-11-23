(ns dict-chrome.typeahead
  (:require [reagent.core :as reagent :refer [atom]]))


(defn phrase-input-view
  [{:keys [current-locale phrase-input-val process-key-event
           translate on-locale-click]}]
  [:div.phrase-input-block
   [:div.input-group
    [:input {:type "text"
             :value @phrase-input-val
             :on-key-down process-key-event
             :on-key-up (fn [event]
                          (when (= (.-key event) "Enter")
                            (translate (-> event .-target .-value))))
             :on-change #(reset! phrase-input-val (-> % .-target .-value))
             :placeholder "start typing here ..."}]
    [:span
     [:button {:on-click on-locale-click} @current-locale]]]])
