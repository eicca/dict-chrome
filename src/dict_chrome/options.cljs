(ns dict-chrome.options
  (:require [reagent.core :as reagent :refer [atom]]
            [dict-chrome.locales :as locales :refer [user-locales]]))

;; TODO move to appropriate place
(extend-type js/HTMLCollection
  ISeqable
  (-seq [array] (array-seq array 0)))

(def message (atom {}))

(defn set-message!
  [content type]
  (reset! message {:content content :type type}))

(defn set-error-message!
  [content]
  (set-message! content "error"))

(defn set-success-message!
  [content]
  (set-message! content "success"))

(defn set-user-locales
  []
  (this-as this (locales/set-user-locales!
                 (-> this .-selectize .getValue))))

(defn save-options
  []
  (let [error-message (locales/validate-user-locales)]
    (if error-message
      (set-error-message! error-message)
      (do (locales/save-user-locales!)
          (set-success-message! "Languages were saved.")))))

(defn languages-select
  []
  [:select {:multiple true
            :value @user-locales}
   (for [locale locales/supported-locales]
     [:option {:value (locale :alpha2)}
      (locale :name)])])

(def selectized-languages-select
  (with-meta languages-select
    {:component-did-mount
     (fn [component]
       (-> (reagent/dom-node component)
         js/jQuery
         (.selectize #js{:plugins #js["remove_button"]})
         (.on "change" set-user-locales)))}))

(defn languages-view
  []
  [:div.section
   [:h1 "Languages"]
   [:hr]
   [:p "Select here languages which you're using "
    "(both known and learning languages)."]
   [selectized-languages-select]
   [:button {:on-click save-options} "Apply"]
   [:br] ;; br is needed because of some weird reagent/react behavior.
   (when-not (empty? @message)
     [:div.message {:class (@message :type)}
      (@message :content)])])

(defn open-shortcuts-page
  []
  ((.. js/chrome -tabs -create)
   #js{:url "chrome://extensions/configureCommands"}))

(defn shortcuts-view
  []
  [:div.section
   [:h1 "Shortcuts"]
   [:hr]
   [:div "You can change pop-up shortcut by following "
    [:a {:href "#" :on-click open-shortcuts-page}
     "this link"] " and adjusting shortcut under 'Smart Translate'."
    ]])

(defn options-view
  []
  [:div#main
   [:header "Smart Translate Options"]
   [languages-view]
   [shortcuts-view]])

(defn ^:export run
  []
  (locales/init
   (fn []
     (reagent/render [options-view] (.-body js/document)))))
