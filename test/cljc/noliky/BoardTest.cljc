(ns noliky.BoardTest
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Player :as PR]
            [noliky.Position :as P]
            [noliky.MoveResult :as M]
            [noliky.Types :as T :refer [first-move next-move]]
            [noliky.Blind :refer [random-moves]]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [schema.test :as st]
            [clojure.test :as t :refer (is deftest)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer (for-all)]
            [clojure.test.check.clojure-test :refer (defspec)]))

(t/use-fixtures :once st/validate-schemas)

(deftest test-first-move
  (let [board (first-move random-moves)]
    (is (= PR/Player2 (BL/whoseTurn board)))
    (is (= 1 (count (BL/occupiedPositions board))))
    (is (false? (BL/isEmpty board)))
    (is (= PR/Player1 (BL/playerAt board (first (BL/occupiedPositions board)))))))

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
      (= PR/Player1 (BL/whoseTurn sb))))))
