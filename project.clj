(defproject krestiky "0.1.0-SNAPSHOT"
  :description "Krestiky and Noliky"
  :url "https://github.com/slava92/krestiky"
  :license {:name "Unlicense"
            :url "http://unlicense.org"}
  :dependencies [[org.clojure/clojurescript "0.0-3211"]
                 [org.clojure/clojure "1.7.0-beta1"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.typed "0.2.84"]]
  ;; core.typed repository:
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :core.typed {:check [krestiky.Board hrest.Board]}
  ;; ClojureScript related
  :plugins [[lein-cljsbuild "1.0.5"]]
  :cljsbuild {:builds
              {:app {:source-paths ["srcjs"]
                     :compiler {:output-to     "out/app.js"
                                :output-dir    "out"
                                :asset-path   "js/out"
                                :optimizations :none
                                :pretty-print  true}}
              ;; {:dev
              ;;  {:source-paths ["srcjs"]
              ;;   :compiler {:output-to "out/main.js"
              ;;              :main 'noliky.Types}}
               ;; :min
               ;; {:source-paths ["srcjs"]
               ;;  :compiler {:output-to "out/main.js"
               ;;             :optimizations :advanced}}
               }})
