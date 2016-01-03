(defproject dict-chrome "0.1.0-SNAPSHOT"
  :description "Smart translate chrome extension."
  :url "https://chrome.google.com/webstore/detail/smart-translate/fmllglnmbaiehbdnnmjppbjjcffnhkcp"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [cljs-ajax "0.5.2"]
                 [reagent "0.5.1"]
                 [lein-cljsbuild/cljs-compat "1.0.0-SNAPSHOT"]
                 [fs "1.1.2"]]

  :plugins [[lein-cljsbuild "1.1.2"]]
  :hooks [leiningen.cljsbuild]

  :mirrors {#"clojars" {:name "Clojar Mirror"
                        :url "https://clojars-mirror.tcrawley.org/repo/"
                        :repo-manager true}}

  :source-paths ["src"]
  :test-paths ["test"]

  :cljsbuild {:builds {:app {:id "dict-chrome"
                             :source-paths ["src"]
                             :compiler {:output-to "extension/js/dict_chrome.js"
                                        :output-dir "extension/js/deps"
                                        :pretty-print true
                                        :optimizations :none}}}}

  :profiles {:dev {}
             :production {:env {:production true}
                          :cljsbuild {:builds {:app
                                               {:compiler
                                                {:optimizations :advanced
                                                 :externs ["externs/chrome.js"]
                                                 :pretty-print false}}}}}})
