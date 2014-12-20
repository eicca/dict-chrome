(ns dict-chrome.api-client
  (:require clojure.walk
            [ajax.core :refer [GET]]
            [dict-chrome.locales :as locales :refer [current-locale]]))

(defn- api-url
  [action]
  (str "http://dict-server.random-data.com" action))

(defn- on-response
  [raw-response handler]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (handler response)))

(defn- get-resource
  [resource-path handler params]
  (GET (api-url resource-path)
       {:params params
        :handler #(on-response % handler)}))

(defn get-suggestions
  [input-value handler]
  (get-resource "/suggestions" handler
                {:phrase input-value
                 :locales locales/user-locales
                 :fallback-locale @current-locale}))

(defn get-translations
  [handler params]
  (get-resource "/translations" handler params))
