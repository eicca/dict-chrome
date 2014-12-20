(ns dict-chrome.popup
  (:require [reagent.core :as reagent]
            [dict-chrome.typeahead :as typeahead]
            [dict-chrome.translation :as translation]
            [weasel.repl :as ws-repl]))

(ws-repl/connect "ws://localhost:9001")

(enable-console-print!)

(defn popup-view
  [_]
  [:div
   [:h3 "Smart Translate"]
   [typeahead/main-view]
   [translation/main-view]])

(defn run
  []
  (reagent/render-component [popup-view] (.-body js/document)))

(.addEventListener js/document "DOMContentLoaded" #(run))
