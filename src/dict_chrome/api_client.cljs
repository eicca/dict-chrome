(ns dict-chrome.api-client
  (:require [ajax.core :refer [GET]]))

(defn api-url
  [action]
  (str "http://dict-server.random-data.com" action))

(defn get-resource
  [resource-path handler params]
  (GET (api-url resource-path)
       {:params params
        :handler handler}))

(defn get-suggestions
  [handler params]
  (get-resource "/suggestions"))

(defn get-translations
  [handler params]
  (get-resource "/translations"))
