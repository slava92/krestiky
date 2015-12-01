(set-env!
  :source-paths   #{"src/cljs"}
  :resource-paths #{"resources/public"}
  :dependencies '[
    [adzerk/boot-cljs            "1.7.170-3"      :scope "test"]
    [adzerk/boot-cljs-repl       "0.3.0"]
    [adzerk/boot-reload          "0.4.2"          :scope "test"]
    [com.cemerick/piggieback     "0.2.1"          :scope "test"]
    [crisptrutski/boot-cljs-test "0.2.0-SNAPSHOT" :scope "test"]
    [org.clojure/clojure         "1.7.0"]
    [org.clojure/clojurescript   "1.7.58"]
    [org.clojure/test.check      "0.9.0"          :scope "test"]
    [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]
    [pandeiro/boot-http          "0.7.1-SNAPSHOT" :scope "test"]
    [re-frame                    "0.5.0"]
    [reagent                     "0.5.1"]
    [weasel                      "0.7.0"          :scope "test"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload    :refer [reload]]
  '[crisptrutski.boot-cljs-test  :refer [test-cljs]]
  '[pandeiro.boot-http    :refer [serve]])

;;; This prevents a name collision WARNING between the test task and
;;; clojure.core/test, a function that nobody really uses or cares
;;; about.
(ns-unmap 'boot.user 'test)

(deftask testing []
  (merge-env! :source-paths #{"test/cljs"})
  (let [nss #{'example.core
              'noliky.BoardTest 'noliky.GameResultTest
              'noliky.MoveResultTest 'noliky.PlayerTest}]
    (task-options! test-cljs {:namespaces nss}))
  identity)

(deftask test []
  (comp (testing)
        (test-cljs :js-env :phantom
                   :exit?  true)))

(deftask auto-test []
  (comp (watch)
        (speak)
        (testing)
        (test-cljs :js-env :phantom)))

(deftask dev []
  (comp (serve :dir "out/")
        (watch)
        (speak)
        (reload :on-jsload 'simpleexample.core)
        (cljs-repl)
        (cljs :source-map true :optimizations :none)))

(deftask build []
  (comp (cljs :optimizations :advanced)))

;; sh => boot dev
;; (start-repl) ???
