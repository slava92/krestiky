(ns noliky.MoveResultTest
  (:require [noliky.MoveResult :as M]
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

(def move-results
  [[true false false (T/->PositionOccupied :PositionOccupied)]
   [false true false (T/->KeepPlaying (T/->Board [] {} :Board) :KeepPlaying)]
   [false false true (T/->GameFinished (T/->Board [] {} :Board) :GameFinished)]])

(defspec move-result-fold
  100
  (for-all [[oc kp gf mr] (gen/elements move-results)]
           (M/foldMoveResult oc
                             (constantly kp)
                             (constantly gf)
                             mr)))
