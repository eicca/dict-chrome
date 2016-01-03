(ns dict-chrome.popup
  (:require [reagent.core :as reagent]
            [dict-chrome.locales :as locales]
            [dict-chrome.typeahead :as typeahead]
            [dict-chrome.translation :as translation]))

(enable-console-print!)

(def options-link
  ;; chrome 40 (str "chrome://extensions?options=" (.. js/chrome -runtime -id)))
  "options.html")

(defn open-options-page
  []
  ((.. js/chrome -tabs -create) (clj->js {:url options-link})))

(defn promt-to-options-view
  [_]
  [:div.promt-to-options
   [:span
    "Setup your languages on the "]
   [:a {:href "#" :on-click open-options-page} "options page."]])

(defn popup-view
  [_]
  [:div
   [:h3 "Smart Translate"
    ;; http://stackoverflow.com/questions/16701082
    [:a.icon-cog {:href "#" :title "Settings" :tabIndex -1
                  :on-click open-options-page} " "]]
   (if (locales/not-enough)
     [promt-to-options-view]
     [:div
      [typeahead/main-view]
      [translation/main-view]])])

(defn ^:export run
  []
  (locales/init
   (fn []
     (reagent/render [popup-view] (.-body js/document)))))
