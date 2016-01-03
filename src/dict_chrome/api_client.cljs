(ns dict-chrome.api-client
  (:require clojure.walk
            [ajax.core :refer [POST json-request-format]]
            [dict-chrome.locales :as locales :refer [current-locale
                                                     user-locales]]))


(defn- api-url
  [action]
  (str "http://translate-service-746109927.eu-west-1.elb.amazonaws.com" action))
  ;; (str "http://localhost:8080" action))

(defn- on-response
  [raw-response handler]
  (let [response (clojure.walk/keywordize-keys raw-response)]
    (handler response)))

(defn- post-resource
  [resource-path handler params]
  (POST (api-url resource-path)
       {:params params
        :format (json-request-format)
        :handler #(on-response % handler)}))

(defn get-suggestions
  [input-value handler]
  (post-resource "/suggestions" handler
                {:query input-value
                 :locales @user-locales
                 :fallback-locale @current-locale}))

(defn get-translations
  [phrase from-locale handler]
  (post-resource "/translations" handler
                {:source from-locale
                 :targets (locales/dest-locales from-locale)
                 :query phrase}))
