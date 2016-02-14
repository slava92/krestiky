(set-env!
 :dependencies
 '[
   [re-frame                    "0.6.0"]
   [reagent                     "0.5.1"]
   [org.clojure/clojure         "1.8.0"]
   [org.clojure/clojurescript   "1.7.228"]
   [prismatic/schema            "1.0.4"]
   [adzerk/boot-cljs            "1.7.228-1" :scope "test"]
   [adzerk/boot-cljs-repl       "0.3.0"     :scope "test"]
   [adzerk/boot-reload          "0.4.5"     :scope "test"]
   [adzerk/boot-test            "1.0.7"     :scope "test"]
   [com.cemerick/piggieback     "0.2.1"     :scope "test"]
   [org.clojure/test.check      "0.9.0"     :scope "test"]
   [org.clojure/tools.nrepl     "0.2.12"    :scope "test"]
   [weasel                      "0.7.0"     :scope "test"]
   ;; boot -d boot-deps ancient
   [boot-deps                   "0.1.6"     :scope "test"]]
 :source-paths   #{"src/cljc" "src/cljs"}
 :resource-paths #{"resources/public"})

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[adzerk.boot-test      :as bt])

;;; This prevents a name collision WARNING between the test task and
;;; clojure.core/test, a function that nobody really uses or cares about.
(ns-unmap 'boot.user 'test)

(deftask dev "Start dev tools (watch, cljs-repl, reload, cljs)"
  []
  (comp
   (watch)
   ;; (speak)
   (cljs-repl)
   (reload :on-jsload 'main.dev/refresh)
   (cljs)))

(deftask testing "Add 'test/cljs/' to :source-paths"
  []
  (merge-env! :source-paths #{"test/cljc"})
  identity)

(deftask test "Run tests"
  []
  (comp (testing)
        (let [nss #{'noliky.BoardTest 'noliky.GameResultTest
                    'noliky.MoveResultTest 'noliky.PlayerTest}]
          (bt/test :namespaces nss))))

(deftask auto-test "Run tests when source code changes"
  []
  (comp (testing)
        (watch)
        (let [nss #{'noliky.BoardTest 'noliky.GameResultTest
                    'noliky.MoveResultTest 'noliky.PlayerTest}]
          (bt/test :namespaces nss))))

(deftask release "Package optimized version"
  []
  (cljs :optimizations :advanced))

;; 1. boot -C dev ;; start watcher, compiler, etc. in backgound
;; 2. C-c M-c ;; connect to backgound server
;; 3. (start-repl) ;; start cljs repl
