(ns dict-chrome.locales
  (:require [reagent.core :as reagent :refer [atom]]))

(def supported-locales
  [{:name "English" :code "en"}
   {:name "German" :code "de"}
   {:name "Russian" :code "ru"}
   {:name "French" :code "fr"}
   {:name "Italian" :code "it"}])

(def current-locale (atom "en"))

(def storage
  (.. js/chrome -storage -sync))

(defn get-user-locales
  [callback]
  (.get storage "userLocales" #(callback (.-userLocales %))))

(defn validate-user-locales
  [user-locales]
  (if (< (count user-locales) 2)
    "select at least 2 languages."
    nil))

(defn set-user-locales!
  [user-locales]
  (if (< (count user-locales) 2)
    "select at least 2 languages."
    (.set storage (clj->js {:userLocales user-locales}))))

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
