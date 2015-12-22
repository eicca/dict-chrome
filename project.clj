(defproject dict-chrome "0.1.0-SNAPSHOT"
  :description "Smart translate chrome extension."
  :url "https://chrome.google.com/webstore/detail/smart-translate/fmllglnmbaiehbdnnmjppbjjcffnhkcp"

  :dependencies [[org.clojure/clojure "1.7.0"]
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
                   :env {:dev? true}
                   :cljsbuild {:builds {:app {:compiler {:source-map true}}}}}
             :production {:env {:production true}
                          :cljsbuild {:builds {:app
                                               {:compiler
                                                {:optimizations :advanced
                                                 :externs ["externs/chrome.js"]
                                                 :pretty-print false}}}}}})
