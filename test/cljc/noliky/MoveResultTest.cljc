(ns noliky.MoveResultTest
  (:require [noliky.Board :as B]
            [noliky.MoveResult :as M]
            [noliky.GameResult :as G]
            [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [schema.test :as st]
            [clojure.test :as t]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer (for-all)]
            [clojure.test.check.clojure-test :refer (defspec)]))

(t/use-fixtures :once st/validate-schemas)

(def dumb-board (B/board [] {}))
(def move-results
  [[true false false (M/position-occupied)]
   [false true false (M/keep-playing dumb-board)]
   [false false true (M/game-finished (B/finished-board dumb-board G/Draw))]])

(defspec move-result-fold
  100
  (for-all [[oc kp gf mr] (gen/elements move-results)]
           (M/foldMoveResult oc
                             (constantly kp)
                             (constantly gf)
                             mr)))
