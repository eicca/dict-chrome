(ns dict-chrome.options
  (:require [reagent.core :as reagent :refer [atom]]
            [dict-chrome.locales :as locales]))

(extend-type js/HTMLCollection
  ISeqable
  (-seq [array] (array-seq array 0)))

(def user-locales (atom []))

(defn update-user-locales
  []
  (this-as this (reset! user-locales
                        (-> this .-selectize .getValue))))

(defn save-options
  []
  (locales/set-user-locales @user-locales))

(defn languages-select
  []
  [:select {:multiple true
            :value @user-locales}
   (for [locale locales/supported-locales]
     [:option {:value (locale :code)
               :on-change update-user-locales}
      (locale :name)])])

(def selectized-languages-select
  (with-meta languages-select
    {:component-did-mount
     (fn [component]
       (-> (reagent/dom-node component)
         js/jQuery
         (.selectize #js{:plugins #js["remove_button"]})
         (.on "change" update-user-locales)))}))

(defn languages-view
  []
  [:div
   [:h1 "Languages"]
   [:hr]
   [:p "Select here languages which you're using
(both known and learning languages)."]
   [selectized-languages-select]])

(defn options-view
  []
  [:div#main
   [:header "Smart Translate Options"]
   [languages-view]
   [:button {:on-click save-options} "Apply"]])

(defn run
  []
  (locales/get-user-locales
   (fn [loaded-locales]
     (reset! user-locales loaded-locales)
     (reagent/render [options-view] (.-body js/document)))))

(.addEventListener js/document "DOMContentLoaded" #(run))
