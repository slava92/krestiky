(defproject krestiky "0.1.0-SNAPSHOT"
  :description "Krestiky and Noliky"
  :url "https://github.com/slava92/krestiky"
  :license {:name "Unlicense"
            :url "http://unlicense.org"}
  :source-paths ["src/cljs"]
  :dependencies [[org.clojure/clojurescript "0.0-3269"]
                 [org.clojure/clojure "1.7.0-beta2"]
                 [reagent "0.5.0"]
                 [re-frame "0.4.0"]
                 [figwheel "0.3.2"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.2.3-SNAPSHOT"]]

  :hooks [leiningen.cljsbuild]

  :profiles {:dev {:plugins [[com.cemerick/austin "0.1.6"]]
                   :cljsbuild
                   {:builds {:client {:source-paths ["dev"]
                                      :compiler
                                      {:main simpleexample.dev
                                       :optimizations :none
                                       :source-map true
                                       :source-map-timestamp true}}}}}

             :prod {:cljsbuild
                    {:builds {:client {:compiler
                                       {:optimizations :advanced
                                        :elide-asserts true
                                        :pretty-print false}}}}}}

  :figwheel {:repl false}

  :cljsbuild {:builds {:client {:source-paths ["src/cljs"]
                                :compiler
                                {:output-dir "js/game"
                                 :output-to "js/game.js"}}}
              :repl
              {:injections
               (require '[cemerick.austin.repls
                          :refer (exec)
                          :rename {exec austin-exec}])}})
;; (cemerick.austin.repls/exec)

;; Figwheel: Starting server at http://localhost:3449
;; Figwheel Config Warning (in project.clj) -- 
;; Your build :output-dir is not in a resources directory.
;; If you are serving your assets (js, css, etc.) with Figwheel,
;; they must be on the resource path for the server.
;; Your :output-dir should match this pattern: (dev-resources|resources)/public

;; Figwheel: focusing on build-ids (client)
;; Compiling "target/client.js" from ["src/cljs" "devsrc"]...
;; WARNING: run! already refers to: #'clojure.core/run! in namespace: reagent.ratom, being replaced by: #'reagent.ratom/run!
