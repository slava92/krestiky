(defproject krestiky "0.1.0-SNAPSHOT"
  :description "Krestiky and Noliky"
  :url "https://github.com/slava92/krestiky"
  :license {:name "Unlicense"
            :url "http://unlicense.org"}
  :source-paths ["src" "srcjs"]
  :dependencies [[org.clojure/clojurescript "0.0-3211"]
                 [org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.typed "0.2.84"]]

  ;; core.typed repository:
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :core.typed {:check [krestiky.Board hrest.Board]}

  ;; ClojureScript related
  :profiles
  {:dev
   {;; :repl-options
    ;; {:init-ns cemerick.austin.bcrepl-sample}
    :plugins [[com.cemerick/austin "0.1.6"]
              [lein-cljsbuild "1.0.5"]]
    :cljsbuild
    {:builds [{:source-paths ["srcjs"]
               :compiler {:output-to "target/classes/public/app.js"
                          :optimizations :simple
                          :pretty-print true}}]}}})
;; (cemerick.austin.repls/exec)
;; 
;; :injections
;; (require '[cemerick.austin.repls
;;            :refer (exec)
;;            :rename {exec austin-exec}])
