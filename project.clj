(defproject krestiky "0.1.0-SNAPSHOT"
  :description "Krestiky and Noliky"
  :url "https://github.com/slava92/krestiky"
  :license {:name "Unlicense"
            :url "http://unlicense.org"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [reagent "0.5.1"]
                 [re-frame "0.5.0"]
                 [com.cemerick/double-check "0.6.1"]]
  :plugins [[lein-cljsbuild "1.1.1"]
            [com.cemerick/clojurescript.test "0.3.3"]]

  :source-paths ["src/cljs"]
  :test-source-paths ["test/cljs"]
  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.0-2"]]
                   :source-paths ["src/dev"]}}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "dev-resources" "figwheel_server.log"
                                    ".nrepl-port"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel true
                        :compiler {:main simpleexample.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "test"
                        :source-paths ["src/cljs" "test/cljs"]
                        :compiler {:output-to "target/cljs/testable.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main simpleexample.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]

              :test-commands {"unit-tests"
                              ["phantomjs" :runner
                               "target/cljs/testable.js"]}}

  :aliases {"units" ["cljsbuild" "test" "unit-tests"]}
  )

;; 1. (start) ;; from src/dev/user.clj
;; 2. (cljs)  ;; from src/dev/user.clj
