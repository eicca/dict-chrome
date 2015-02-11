(defproject dict-chrome "0.1.0-SNAPSHOT"
  :description "Smart translate chrome extension."
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2760"]
                 [cljs-ajax "0.2.6"]
                 [reagent "0.5.0-alpha2"]
                 [com.gibbonspace/clj-webdriver "0.7.0-SNAPSHOT"]
                 [weasel "0.4.2"]
                 [com.cemerick/piggieback "0.1.3"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]
  :hooks [leiningen.cljsbuild]

  :source-paths ["src"]
  :test-paths ["test"]

  :cljsbuild {:builds {:app {:id "dict-chrome"
                             :source-paths ["src"]
                             :compiler {:output-to "extension/js/dict_chrome.js"
                                        :output-dir "extension/js/deps"
                                        :pretty-print true
                                        :optimizations :none}}}}

  :profiles {:dev {:repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[pjstadig/humane-test-output "0.6.0"]]

                   :plugins [[com.cemerick/clojurescript.test "0.3.3"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev? true}

                   :cljsbuild {:builds {:app {:compiler {:source-map true}}
                                        :test {:source-paths ["src" "test"]
                                               :compiler {:output-to "test/target/test.js"
                                                          :optimizations :whitespace
                                                          :pretty-print true}}}
                               :test-commands {"unit" ["slimerjs" :runner
                                                       "test/vendor/console-polyfill.js"
                                                       "test/target/test.js"]}}}

  :production {:env {:production true}

                          :cljsbuild {:builds {:app
                                               {:compiler
                                                {:optimizations :advanced
                                                 :externs ["externs/chrome.js"]
                                                 :pretty-print false}}}}}})
