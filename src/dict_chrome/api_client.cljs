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
  (locales/get-user-locales
   (fn [user-locales]
     (get-resource "/suggestions" handler
                   {:phrase input-value
                    :locales user-locales
                    :fallback-locale @current-locale}))))

(defn get-translations
  [phrase from-locale handler]
  (locales/get-dest-locales from-locale
   (fn [dest-locales]
     (get-resource "/translations" handler
                   {:from from-locale
                    :dest-locales dest-locales
                    :phrase phrase}))))
