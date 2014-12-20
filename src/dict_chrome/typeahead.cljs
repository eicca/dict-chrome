(ns dict-chrome.typeahead
  (:require [reagent.core :as reagent :refer [atom]]
            [dict-chrome.api-client :as api-client]
            [dict-chrome.translation :as translation]
            [dict-chrome.active-view :as active-view]
            [dict-chrome.locales :as locales :refer [current-locale]]))

(def suggestions (atom {}))

(def active-suggestion-index (atom 0))

(def phrase-input-val (atom ""))

(defn indexate
  [seq]
  (map-indexed (fn [idx itm] (conj {:index idx} itm))
               seq))

(defn suggestions-loaded
  [response]
  (reset! active-suggestion-index 0)
  (reset! suggestions (indexate (take 7 response)))
  (when-let [first-item (first response)]
    (locales/set! (first-item :locale))))

(defn autocomplete
  [_ _ _ input-value]
  (active-view/set! :suggestions)
  (when (> (count input-value) 2)
    (api-client/get-suggestions input-value suggestions-loaded)))

(defn change-active-suggestion
  [offset]
  (let [new-index (+ @active-suggestion-index offset)]
    (when (and (>= new-index 0) (< new-index (count @suggestions)))
      (reset! active-suggestion-index new-index))))

(defn apply-suggestion
  []
  (let [suggestion (first
                    (filter #(= (% :index) @active-suggestion-index)
                            @suggestions))]
    (reset! phrase-input-val (suggestion :phrase))))

(defn process-key-event
  [event]
  (case (.-key event)
    "Enter" (translation/translate! @phrase-input-val @current-locale)
    "ArrowDown" (change-active-suggestion 1)
    "ArrowUp" (change-active-suggestion -1)
    "Tab" (#(.preventDefault event)
           (apply-suggestion))
    :default))

(defn phrase-input-view
  [_]
  [:div.phrase-input-block
   [:div.input-group
    [:input {:type "text"
             :value @phrase-input-val
             :on-key-down #(process-key-event %)
             :on-change #(reset! phrase-input-val (-> % .-target .-value))
             :placeholder "start typing here ..."}]
    [:span
     [:button {:on-click locales/set-next!} @current-locale]]]])

(defn suggestion-view
  [{:keys [phrase locale index]}]
  [:li {:class (when (= index @active-suggestion-index) "active")
        :on-click #(translation/translate! phrase locale)}
   phrase])

(defn suggestions-view
  [_]
  [:ul.suggestions (for [suggestion @suggestions]
                     [suggestion-view suggestion])])

(defn main-view
  [_]
  (add-watch phrase-input-val :phrase-input-watcher autocomplete)
  [:div
   [phrase-input-view]
   (when (active-view/active? :suggestions)
     [suggestions-view])])
