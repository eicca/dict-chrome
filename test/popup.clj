(ns dict-chrome.test.popup
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [clj-webdriver.core :refer [key-code]]))

(def file-url "file:///Users/mike/dev/dict/dict-chrome/extension/popup.html")

(deftest test-suggestions-after-input
  (set-driver! {:browser :firefox})
  (implicit-wait 3000)
  (to file-url)
  (find-element {:tag :body})
  (input-text "input" "somewher")
  (is (exists? (find-element {:tag :li, :text "somewhere"})))
  (input-text "input" "e")
  (send-keys "input" (key-code :enter))
  (is (exists? (find-element {:tag :a, :text "irgendwo"})))
  (quit))

(run-tests)
