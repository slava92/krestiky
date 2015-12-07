(ns noliky.BoardTest
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Position :as P]
            [noliky.MoveResult :as M]
            [noliky.Types :as T :refer [first-move next-move]]
            [noliky.Blind :refer [random-moves]]
            [clojure.test :as t :refer (is deftest)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer (for-all)]
            [clojure.test.check.clojure-test :refer (defspec)]))

(deftest test-first-move
  (let [board (first-move random-moves)]
    (is (= T/Player2 (BL/whoseTurn board)))
    (is (= 1 (count (BL/occupiedPositions board))))
    (is (false? (BL/isEmpty board)))
    (is (= T/Player1 (BL/playerAt board (first (BL/occupiedPositions board)))))))

(defspec test-second-move
  100
  (for-all
   [gen/nat]
   (let [fb (first-move random-moves)
         sb (M/keepPlaying (B/--> (next-move random-moves fb) fb))]
     (and
      (= 2 (count (BL/occupiedPositions sb)))
      (not= (first (BL/occupiedPositions sb))
            (last (BL/occupiedPositions sb)))
      (= T/Player1 (BL/whoseTurn sb))))))
