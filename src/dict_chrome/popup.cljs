(ns dict-chrome.popup
  (:require clojure.walk
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]
            [dict-chrome.api-client :as api-client]))

(enable-console-print!)

(defn indexate
  [seq]
  (map-indexed (fn [idx itm] (conj {:index idx} itm)) seq))

; Atoms ==============

(def active-view (atom (keyword "")))

(defn show!
  [view-name]
  (when-not (= @active-view view-name)
    (reset! active-view view-name)))

(def app-translation (atom {}))

(def suggestions (atom {}))

(def active-suggestion-index (atom 0))

(def phrase-input-val (atom ""))


; Locales ===============

(def current-locale (atom "en"))

; TODO take it from settings.
(def user-locales ["en" "de" "ru"])

(def cycled-user-locales (cycle user-locales))

(defn set-next-current-locale!
  []
  (let [current-locale-index (.indexOf (to-array user-locales) @current-locale) next-locale (nth cycled-user-locales (inc current-locale-index))]
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
  (str "http://dict-server.random-data.com" action))

(defn app-translation-loaded
  [raw-response]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (reset! app-translation response))
  (show! :app-translation))

(defn suggestions-loaded
  [raw-response]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (reset! active-suggestion-index 0)
    (reset! suggestions (indexate (take 7 response)))
    (when-let [first-item (first response)]
      (reset! current-locale (first-item :locale))))
  (show! :suggestions))

(defn translate
  [input-phrase]
  (api-client/get-translations app-translation-loaded
                               {:from @current-locale
                                :dest-locales (dest-locales)
                                :phrase input-phrase}))

; Utils ==========

(defn play-sound
  [sound-url]
  (let [audio (js/Audio. sound-url)]
    (.play audio)))

; DIM =============

(defn autocomplete
  [_ _ _ input-value]
  (when (> (count input-value) 2)
    (api-client/get-suggestions suggestions-loaded
                                {:phrase input-value
                                 :locales user-locales
                                 :fallback-locale @current-locale})))

(defn change-active-suggestion
  [new-index]
  (when (and (>= new-index 0) (< new-index (count @suggestions)))
    (reset! active-suggestion-index new-index)))

(defn apply-suggestion
  []
  (let [suggestion (first
                    (filter #(= (% :index) @active-suggestion-index)
                            @suggestions))]
    (reset! phrase-input-val (suggestion :phrase))))

(defn process-key-event
  [event]
  (case (.-key event)
    "ArrowDown" (change-active-suggestion (+ @active-suggestion-index 1))
    "ArrowUp" (change-active-suggestion (- @active-suggestion-index 1))
    "Tab" (#(.preventDefault event)
           (apply-suggestion))
    :default))

(defn phrase-input-view
  []
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
     [:button {:on-click set-next-current-locale!} @current-locale]]]])


(defn suggestion-view
  [{:keys [phrase locale index]}]
  [:li {:class (when (= index @active-suggestion-index) "active")
        :on-click #(api-client/get-translations
                    app-translation-loaded
                    {:phrase phrase :from locale
                     :dest-locales (dest-locales)})}
   phrase])

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
   [phrase-input-view]
   (case @active-view
     :suggestions [suggestions-view]
     :app-translation [app-translation-view]
     [:div.empty-view])])

(defn run
  []
  (reagent/render-component [popup-view] (.-body js/document))
  (add-watch phrase-input-val :phrase-input-watcher autocomplete))

(.addEventListener js/document "DOMContentLoaded" #(run))
