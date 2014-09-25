(ns dict-chrome.popup
  (:require clojure.walk
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]))

(enable-console-print!)

; Atoms ==============

(def app-translation (atom {}))

(def suggestions (atom {}))

(def fallback-locale (atom "en"))

; TODO take it from settings.
(def user-locales ["en" "de" "ru"])

(defn dest-locales
  []
  (remove #(= % @fallback-locale) user-locales))

; API ================

(defn api-url
  [action]
  (str "http://localhost:3000" action))

(defn app-translation-loaded
  [raw-response]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (reset! app-translation response)))

(defn suggestions-loaded
  [raw-response]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (reset! suggestions (take 7 response))
    (when-let [first-item (first response)]
      (reset! fallback-locale (first-item :locale)))))

(defn get-suggestions
  [params]
  (GET (api-url "/suggestions")
       {:params params
        :handler suggestions-loaded}))

(defn get-translations
  [params]
  (GET (api-url "/translations")
       {:params params
        :handler app-translation-loaded}))

(defn translate
  [input-phrase]
  (get-translations {:from @fallback-locale :dest-locales (dest-locales) :phrase input-phrase}))

; Utils ==========

(defn play-sound
  [sound-url]
  (let [audio (js/Audio. sound-url)]
    (.play audio)))

; DOM =============

(defn autocomplete
  [input-value]
  (when (> (count input-value) 2)
    (get-suggestions {:phrase input-value
                      :locales user-locales
                      :fallback-locale @fallback-locale})))

(defn process-key-event
  [event])
  ; (case (.-key event)))

(defn suggestion-view
  [{:keys [phrase locale]}]
  [:li {:on-click #(get-translations
                     {:phrase phrase :from locale :dest-locales (dest-locales)})}
   phrase])

(defn typeahead-view
  []
  [:div.typeahead
   [:div.input-group
    [:input {:type "text"
             :on-key-down process-key-event
             :on-key-up (fn [event]
                          (when (= (.-key event) "Enter")
                            (translate (-> event .-target .-value))))
             :on-change #(autocomplete (-> % .-target .-value))
             :placeholder "Type to translate.."}]
    [:span
     [:button @fallback-locale]]]
   (when-not (empty? @suggestions)
     [:ul (for [suggestion @suggestions]
            [suggestion-view suggestion])])])

(defn sound-view
  [sound-url]
  (when sound-url
    [:button.sound {:on-click #(play-sound sound-url)}]))

(defn source-view
  [source-url]
  (when source-url
    [:a {:href source-url :target "_blank"} "Source ->"]))

(defn translation-view
  [translation]
  [:li {:class (translation :source-name)}
   [:span (translation :phrase)]
   [:span.links-block
    [sound-view (first (translation :sounds))]
    [source-view (translation :source-url)]]])

(defn meta-translation-view
  [meta-translation]
  [:div
   [:div.meta-translation-header
    [:span "Translating to "] [:span (meta-translation :dest)]
    [sound-view (meta-translation :sound)]
    [source-view (meta-translation :source-url)]]
   [:ul.translations (for [translation (take 3 (meta-translation :translations))]
                       ^{:key (translation :phrase)} [translation-view translation])]])

(defn app-translation-view
  [_]
  [:div
   [:div.from-phrase
    [:span (@app-translation :phrase)]
    [sound-view (@app-translation :sounds)]]
   [:ul (for [meta-translation (@app-translation :meta-translations)]
          ^{:key (meta-translation :dest)} [meta-translation-view meta-translation])]])

(defn popup-view
  [_]
  [:div
   [:h3 "Smart Translate"]
   (if (empty? @app-translation)
     [typeahead-view]
     [app-translation-view])])

(defn run
  []
  (reagent/render-component [popup-view] (.-body js/document)))

(.addEventListener js/document "DOMContentLoaded" #(run))

