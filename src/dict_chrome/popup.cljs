(ns dict-chrome.popup
  (:require [reagent.core :as reagent]
            [dict-chrome.typeahead :as typeahead]
            [dict-chrome.translation :as translation]
            [weasel.repl :as ws-repl]))

(ws-repl/connect "ws://localhost:9001")

(enable-console-print!)

(def options-link
  ;; chrome 40 (str "chrome://extensions?options=" (.. js/chrome -runtime -id)))
  "options.html")

(defn open-options-page
  []
  ((.. js/chrome -tabs -create) (clj->js {:url options-link})))

(defn popup-view
  [_]
  [:div
   [:h3 "Smart Translate"
    [:a.icon-cog {:href "#" :title "Settings"
                  :on-click open-options-page} " "]]
   [typeahead/main-view] ;; TODO focus on render
   [translation/main-view]])

(defn run
  []
  (reagent/render-component [popup-view] (.-body js/document)))

(.addEventListener js/document "DOMContentLoaded" #(run))
