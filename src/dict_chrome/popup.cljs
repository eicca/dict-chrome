(ns dict-chrome.popup
  (:require clojure.walk
            [reagent.core :as reagent :refer [atom]]
            [dict-chrome.api-client :as api-client]
            [dict-chrome.typeahead :as typeahead]
            [dict-chrome.locales :as locales :refer [current-locale]]
            [dict-chrome.active-view :as active-view]
            [weasel.repl :as ws-repl]))

(ws-repl/connect "ws://localhost:9001")

(enable-console-print!)

; Atoms ==============

(def app-translation (atom {}))

; API ================

(defn app-translation-loaded
  [raw-response]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (reset! app-translation response)))

(defn translate
  [input-phrase locale]
  (active-view/set! :translation)
  (api-client/get-translations app-translation-loaded
                               {:from locale
                                :dest-locales (locales/dest-locales)
                                :phrase input-phrase}))

; Utils ==========

(defn play-sound
  [sound-url]
  (let [audio (js/Audio. sound-url)]
    (.play audio)))

; DIM =============

(defn sound-view
  [sound-url]
  (when sound-url
    [:button.sound.icon-volume {:on-click #(play-sound sound-url)} " "]))

(defn source-view
  [source-url]
  (when source-url
    [:a.icon-share {:href source-url :target "_blank"} " "]))

(defn lexical-view
  [definitions]
  [:ul.lexical (for [definition definitions]
                 definition)])

(defn translation-view
  [translation]
  [:li {:class (translation :source-name)}
   [:a {:href (translation :source-url) :target "_blank"} (translation :phrase)]
   [lexical-view (translation :lexical)]
   [sound-view (first (translation :sounds))]])

(defn meta-translation-view
  [meta-translation]
  [:div
   [:div.meta-translation-header
    [:div.language-block
     [:span] [:span (meta-translation :dest)]
     [source-view (meta-translation :source-url)]]]
   [:ul.translations (for [translation (take 3 (meta-translation :translations))]
                       ^{:key (translation :phrase)} [translation-view translation])]])

(defn app-translation-view
  [_]
  [:div
   [:div.from-phrase
    [:span "Translating: "]
    [:a {:href (@app-translation :wiktionary-link) :target "_blank"} (@app-translation :phrase)]]
   [:ul (for [meta-translation (@app-translation :meta-translations)]
          ^{:key (meta-translation :dest)} [meta-translation-view meta-translation])]])

(defn popup-view
  [_]
  [:div
   [:h3 "Smart Translate"]
   [typeahead/main-view translate active-view]
   (when (active-view/active? :translation)
     [app-translation-view])])

(defn run
  []
  (reagent/render-component [popup-view] (.-body js/document)))

(.addEventListener js/document "DOMContentLoaded" #(run))
