(ns dict-chrome.test.popup
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [clj-webdriver.core :refer [key-code]]))

(def file-url "file:///Users/mdyakov/dev/stuff/dict-chrome/extension/popup.html")

(defn prepare-env
  []
  (set-driver! {:browser :firefox})
  (to file-url)
  (implicit-wait 3000))

(deftest test-suggestions-after-input
  (refresh)
  (find-element {:tag :body})
  (input-text "input" "somewher")
  (is (not= nil (find-element {:tag :li, :text "somewhere"}))))

(deftest test-phrase-translation
  (refresh)
  (find-element {:tag :body})
  (input-text "input" "somewhere")
  (send-keys "input" (key-code :enter))
  (is (not= nil (find-element {:tag :a, :text "irgendwo"})))
  (is (exists? "button.sound")))

(deftest test-switch-locale
  (refresh)
  (find-element {:tag :body})
  (input-text "input" "hello")
  (is (not= nil (find-element {:tag :button, :text "en"})))
  (click ".phrase-input-block button")
  (is (not= nil (find-element {:tag :button, :text "de"}))))


(prepare-env)
(run-tests)
(quit)
