(defproject dict-chrome "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [cljs-ajax "0.2.6"]
                 [reagent "0.4.2"]
                 [com.gibbonspace/clj-webdriver "0.7.0-SNAPSHOT"]
                 [com.cemerick/piggieback "0.1.3"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :source-paths ["src"]
  :test-paths ["test"]

  :cljsbuild {:builds [{:id "dict-chrome"
                        :source-paths ["src"]
                        :compiler {:output-to "extension/js/dict_chrome.js"
                                   :output-dir "extension/js/deps"
                                   :pretty-print true
                                   :optimizations :none
                                   :source-map true}}]})
