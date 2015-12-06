(ns noliky.MoveResultTest
  (:require [noliky.MoveResult :as M]
            [noliky.Types :as T]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer (for-all)]
            [clojure.test.check.clojure-test :refer (defspec)]))

(def move-results
  [[true false false (T/->PositionOccupied :PositionOccupied)]
   [false true false (T/->KeepPlaying nil :KeepPlaying)]
   [false false true (T/->GameFinished nil :GameFinished)]])

(defspec move-result-fold
  100
  (for-all [[oc kp gf mr] (gen/elements move-results)]
           (M/foldMoveResult oc
                             (constantly kp)
                             (constantly gf)
                             mr)))
