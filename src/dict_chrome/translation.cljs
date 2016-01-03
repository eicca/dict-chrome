(ns dict-chrome.translation
  (:require clojure.walk
            [reagent.core :as reagent :refer [atom]]
            [dict-chrome.api-client :as api-client]
            [dict-chrome.active-view :as active-view]
            [dict-chrome.locales :as locales :refer [current-locale]]))

(def app-translation (atom {}))

(defn app-translation-loaded
  [response]
  (reset! app-translation response))

(defn translate!
  [input-phrase locale]
  (active-view/set! :translation)
  (api-client/get-translations input-phrase locale app-translation-loaded))

(defn play-sound
  [sound-url]
  (let [audio (js/Audio. sound-url)]
    (.play audio)))

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
  [:li {:class (translation :origin-name)}
   [:a {:href (translation :web-url) :target "_blank"} (translation :translated-text)]
   [lexical-view (translation :lexical)]
   [sound-view (first (translation :sounds))]])

(defn meta-translation-view
  [meta-translation]
  [:div
   [:div.meta-translation-header
    [:div.language-block
     [:span] [:span (meta-translation :target)]
     [source-view (meta-translation :web-url)]]]
   [:ul.translations (for [translation (take 3 (meta-translation :meanings))]
                       ^{:key (translation :translated-text)}
                       [translation-view translation])]])

(defn app-translation-view
  [_]
  [:div
   [:div.from-phrase
    [:span "Translating: "]
    [:a {:href (@app-translation :wiktionary-link) :target "_blank"}
     (@app-translation :query)]]
   [:ul (for [meta-translation (@app-translation :translations)]
          ^{:key (meta-translation :target)}
          [meta-translation-view meta-translation])]])

(defn main-view
  []
   (when (active-view/active? :translation)
     [app-translation-view]))
