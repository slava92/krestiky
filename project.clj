(defproject krestiky "0.1.0-SNAPSHOT"
  :description "Krestiky and Noliky"
  :url "https://github.com/slava92/krestiky"
  :license {:name "Unlicense"
            :url "http://unlicense.org"}
  :source-paths ["src/cljs"]
  :dependencies [[org.clojure/clojurescript "0.0-3269"]
                 [org.clojure/clojure "1.7.0-beta2"]
                 [reagent "0.5.0"]
                 [re-frame "0.4.0"]]

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.3"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]

                        :figwheel {:on-jsload "simpleexample.core/client"}

                        :compiler {:main simpleexample.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main simpleexample.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]})
