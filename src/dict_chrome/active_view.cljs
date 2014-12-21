(ns dict-chrome.active-view
  (:require [reagent.core :as reagent :refer [atom]]))

(def ^:private active-view-state (atom  ""))

(defn set!
  [view-name]
  (reset! active-view-state view-name))

(defn active?
  [view-name]
  (= @active-view-state view-name))
