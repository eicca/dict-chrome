(ns dict-chrome.locales
  (:require [reagent.core :as reagent :refer [atom]]))

(def supported-locales
  (js->clj (.-locale_codes js/dict_chrome) :keywordize-keys true))

(def current-locale (atom ""))

(def user-locales (atom []))

(def storage
  (.. js/chrome -storage -sync))

(defn init
  [callback]
  (.get storage "userLocales"
        (fn [res]
          (reset! user-locales (.-userLocales res))
          (reset! current-locale (first @user-locales))
          (callback))))

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
  []
  (let [broken-rule (first-broken-rule @user-locales)]
    (if broken-rule
      (broken-rule :message)
      nil)))

(defn set-user-locales!
  [new-user-locales]
  (reset! user-locales new-user-locales))

(defn save-user-locales!
  []
  (.set storage (clj->js {:userLocales @user-locales})))

(defn- next-locale
  []
  (let [current-locale-index (.indexOf (to-array @user-locales) @current-locale)
        next-locale (nth (cycle @user-locales) (inc current-locale-index))]
    next-locale))

(defn dest-locales
  [from-locale]
  (remove #(= % from-locale) @user-locales))

(defn set-next!
  []
  (reset! current-locale (next-locale)))

(defn set!
  [value]
  (reset! current-locale value))
