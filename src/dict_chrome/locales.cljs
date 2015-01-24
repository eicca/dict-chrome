(ns dict-chrome.locales
  (:require [reagent.core :as reagent :refer [atom]]))

(def supported-locales
  (js->clj (.-locale_codes js/dict_chrome) :keywordize-keys true))

(def current-locale (atom "en"))

(def storage
  (.. js/chrome -storage -sync))

(defn get-user-locales
  [callback]
  (.get storage "userLocales" #(callback (.-userLocales %))))

(def validation-rules
  [{:cond #(< (count %) 2)
    :message "Select at least 2 languages."}
   {:cond #(> (count %) 4)
    :message "Select at most 4 languages."}])

(defn- first-broken-rule
  [user-locales]
  (first (filter
          (fn [rule] ((rule :cond) user-locales))
          validation-rules)))

(defn validate-user-locales
  [user-locales]
  (let [broken-rule (first-broken-rule user-locales)]
    (if broken-rule
      (broken-rule :message)
      nil)))

(defn set-user-locales!
  [user-locales]
  (.set storage (clj->js {:userLocales user-locales})))

(defn- next-locale
  [current-locale user-locales]
  (let [current-locale-index (.indexOf (to-array user-locales) current-locale)
        next-locale (nth (cycle user-locales) (inc current-locale-index))]
    next-locale))

(defn get-dest-locales
  [from-locale callback]
  (get-user-locales
   (fn [user-locales]
     (callback (remove #(= % from-locale) user-locales)))))

(defn set-next!
  []
  (get-user-locales
   (fn [user-locales]
     (reset! current-locale (next-locale @current-locale user-locales)))))

(defn set!
  [value]
  (reset! current-locale value))
