(ns dict-chrome.popup
  (:require clojure.walk
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]))

(enable-console-print!)

; Atoms ==============

(def active-view (atom (keyword "")))

(defn show!
  [view-name]
  (when-not (= @active-view view-name)
    (reset! active-view view-name)))

(def app-translation (atom {}))

(def suggestions (atom {}))

; Locales ===============

(def current-locale (atom "en"))

; TODO take it from settings.
(def user-locales ["en" "de" "ru"])

(def cycled-user-locales (cycle user-locales))

(defn set-next-current-locale!
  []
  (let [current-locale-index (.indexOf (to-array user-locales) @current-locale)
        next-locale (nth cycled-user-locales (inc current-locale-index))]
    (reset! current-locale next-locale))
  ; (case @active-view
  ;   :suggestions [suggestions-view]
  ;   :app-translation [app-translation-view]
  )

(defn dest-locales
  []
  (remove #(= % @current-locale) user-locales))

; API ================

(defn api-url
  [action]
  (str "http://stormy-caverns-7598.herokuapp.com" action))

(defn app-translation-loaded
  [raw-response]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (reset! app-translation response))
  (show! :app-translation))

(defn suggestions-loaded
  [raw-response]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (reset! suggestions (take 7 response))
    (when-let [first-item (first response)]
      (reset! current-locale (first-item :locale))))
  (show! :suggestions))

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
  (get-translations {:from @current-locale :dest-locales (dest-locales) :phrase input-phrase}))

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
                      :fallback-locale @current-locale})))

(defn process-key-event
  [event])
; (case (.-key event)))

(defn suggestion-view
  [{:keys [phrase locale]}]
  [:li {:on-click #(get-translations
                     {:phrase phrase :from locale :dest-locales (dest-locales)})}
   phrase])

(defn phrase-input-view
  []
  [:div.phrase-input-block
   [:div.input-group
    [:input {:type "text"
             :on-key-down process-key-event
             :on-key-up (fn [event]
                          (when (= (.-key event) "Enter")
                            (translate (-> event .-target .-value))))
             :on-change #(autocomplete (-> % .-target .-value))
             :placeholder "start typing here ..."}]
    [:span
     [:button {:on-click set-next-current-locale!} @current-locale]]]])

(defn suggestions-view
  []
  [:ul.suggestions (for [suggestion @suggestions]
                     [suggestion-view suggestion])])

(defn sound-view
  [sound-url]
  (when sound-url
    [:button.sound.icon-volume {:on-click #(play-sound sound-url)} " "]))

(defn source-view
  [source-url]
  (when source-url
    [:a.icon-share {:href source-url :target "_blank"} " "]))

(defn translation-view
  [translation]
  [:li {:class (translation :source-name)}
   [:a {:href (translation :source-url)} (translation :phrase)]
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
    [:a {:href (@app-translation :wiktionary-link) :target "_blank"} (@app-translation :phrase)]]
   [:ul (for [meta-translation (@app-translation :meta-translations)]
          ^{:key (meta-translation :dest)} [meta-translation-view meta-translation])]])

(defn popup-view
  [_]
  [:div
   [:h3 "Smart Translate"]
   [phrase-input-view]
   (case @active-view
     :suggestions [suggestions-view]
     :app-translation [app-translation-view]
     [:div.empty-view])])

(defn run
  []
  (reagent/render-component [popup-view] (.-body js/document)))

(.addEventListener js/document "DOMContentLoaded" #(run))

