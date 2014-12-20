(ns dict-chrome.api-client
  (:require [ajax.core :refer [GET]]
            [dict-chrome.locales :as locales :refer [current-locale]]))

(defn api-url
  [action]
  (str "http://dict-server.random-data.com" action))

(defn get-resource
  [resource-path handler params]
  (GET (api-url resource-path)
       {:params params
        :handler handler}))

(defn get-suggestions
  [input-value handler]
  (get-resource "/suggestions" handler
                {:phrase input-value
                 :locales locales/user-locales
                 :fallback-locale @current-locale}))

(defn get-translations
  [handler params]
  (get-resource "/translations" handler params))
