(ns dict-chrome.locales
  (:require [reagent.core :as reagent :refer [atom]]))

;; This should be a reagent atom.
(def current-locale (atom "en"))

; TODO take it from settings.
(def user-locales ["en" "de" "ru"])

(def ^:private cycled-user-locales (cycle user-locales))

(defn next-locale
  [current-locale]
  (let [current-locale-index (.indexOf (to-array user-locales) current-locale)
        next-locale (nth cycled-user-locales (inc current-locale-index))]
    next-locale))

(defn dest-locales
  []
  (remove #(= % @current-locale) user-locales))

(defn set-next!
  []
  (reset! current-locale (next-locale @current-locale)))

(defn set!
  [value]
  (reset! current-locale value))
