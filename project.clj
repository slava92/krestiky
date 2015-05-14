(defproject krestiky "0.1.0-SNAPSHOT"
  :description "Krestiky and Noliky"
  :url "https://github.com/slava92/krestiky"
  :license {:name "Unlicense"
            :url "http://unlicense.org"}
  :source-paths ["src/clj" "src/cljs"]
  :dependencies [[org.clojure/clojurescript "0.0-3269"]
                 [org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.typed "0.2.84"]]

  ;; core.typed repository:
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :core.typed {:check [krestiky.Board hrest.Board]}

  ;; ClojureScript related
  :profiles
  {:dev
   {:plugins [[com.cemerick/austin "0.1.6"]
              [lein-cljsbuild "1.0.5"]]
    :cljsbuild
    {:builds
     ;; This build has the lowest level of optimizations, so it is
     ;; useful when debugging the app.
     {:dev {:source-paths ["src/cljs"]
            :jar true
            :compiler {:output-to "resources/public/js/main-debug.js"
                       :optimizations :whitespace
                       :pretty-print true}}
      ;; This build has the highest level of optimizations, so it is
      ;; efficient when running the app in production.
      :prod {:source-paths ["src/cljs"]
             :compiler {:output-to "resources/public/js/main.js"
                        :optimizations :advanced
                        :pretty-print false}}
      ;; This build is for the ClojureScript unit tests that will
      ;; be run via PhantomJS.
      :test {:source-paths ["src/cljs"] ;; "test/cljs"]
             :compiler {:output-to "resources/private/js/unit-test.js"
                        :optimizations :whitespace
                        :pretty-print true}}}}}
   :repl
   {:injections
    (require '[cemerick.austin.repls
               :refer (exec)
               :rename {exec austin-exec}])}})
;; (cemerick.austin.repls/exec)
