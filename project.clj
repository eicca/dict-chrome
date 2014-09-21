(defproject dict-chrome "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [cljs-ajax "0.2.6"]
                 [reagent "0.4.2"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild {:builds [{:id "dict-chrome"
                        :source-paths ["src"]
                        :compiler {:output-to "extension/js/dict_chrome.js"
                                   :output-dir "extension/js/deps"
                                   :pretty-print true
                                   :optimizations :none
                                   :source-map true}}]})

