(defproject krestiky "0.1.0-SNAPSHOT"
  :description "Krestiky and Noliky"
  :url "https://github.com/slava92/krestiky"
  :license {:name "Unlicense"
            :url "http://unlicense.org"}
  :dependencies [[org.clojure/clojurescript "0.0-3308"]
                 [org.clojure/clojure "1.7.0-beta2"]
                 [reagent "0.5.0"]
                 [re-frame "0.4.1"]
                 [com.cemerick/double-check "0.6.1"]]
  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.3"]
            [com.cemerick/clojurescript.test "0.3.3"]
            [com.cemerick/austin "0.1.6"]]

  :source-paths ["src/cljs"]
  :test-source-paths ["test/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]

                        :figwheel {:on-jsload "simpleexample.core/client"}

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

;; (cemerick.austin.repls/exec)
