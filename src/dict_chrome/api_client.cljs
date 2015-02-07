(ns dict-chrome.api-client
  (:require clojure.walk
            [ajax.core :refer [GET]]
            [dict-chrome.locales :as locales :refer [current-locale
                                                     user-locales]]))

(defn- api-url
  [action]
  (str "http://dict-server.random-data.com" action))
  ;; (str "http://localhost:3000" action))

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
                 :locales @user-locales
                 :fallback-locale @current-locale}))

(defn get-translations
  [phrase from-locale handler]
  (get-resource "/translations" handler
                {:from from-locale
                 :dest-locales (locales/dest-locales from-locale)
                 :phrase phrase}))
