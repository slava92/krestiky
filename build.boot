(set-env!
 :source-paths   #{"src/cljs"}
 :resource-paths #{"resources/public"}
 :dependencies
 '[
   [adzerk/boot-cljs            "1.7.170-3"      :scope "test"]
   [adzerk/boot-cljs-repl       "0.3.0"          :scope "test"]
   [adzerk/boot-reload          "0.4.2"          :scope "test"]
   [com.cemerick/piggieback     "0.2.1"          :scope "test"]
   [crisptrutski/boot-cljs-test "0.2.0-SNAPSHOT" :scope "test"]
   [org.clojure/test.check      "0.9.0"          :scope "test"]
   [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]
   [weasel                      "0.7.0"          :scope "test"]
   [org.clojure/clojure         "1.7.0"]
   [org.clojure/clojurescript   "1.7.189"]
   [re-frame "0.5.0"]
   [reagent "0.5.1"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]])

;;; This prevents a name collision WARNING between the test task and
;;; clojure.core/test, a function that nobody really uses or cares about.
(ns-unmap 'boot.user 'test)

(deftask dev "Start dev tools (watch, cljs-repl, reload, cljs)"
  []
  (comp
   (watch)
   (speak)
   (cljs-repl)
   (reload :on-jsload 'simpleexample.dev/refresh)
   (cljs)))

(deftask testing "Add 'test/cljs/' to :source-paths"
  []
  (merge-env! :source-paths #{"test/cljs"})
  (let [nss #{'noliky.BoardTest 'noliky.GameResultTest
              'noliky.MoveResultTest 'noliky.PlayerTest
              'example.core}]
    (task-options! test-cljs {:namespaces nss}))
  identity)

(deftask test "Run tests"
  []
  (comp (testing)
        (test-cljs :js-env :phantom
                   :exit?  true)))

(deftask auto-test "Run tests when source code changes"
  []
  (comp (testing)
        (watch)
        (test-cljs :js-env :phantom)))

(deftask release "Package optimized version"
  []
  (cljs :optimizations :advanced))

;; 1. boot -C dev ;; start watcher, compiler, etc. in backgound
;; 2. C-c M-c ;; connect to backgound server
;; 3. (start-repl) ;; start cljs repl
