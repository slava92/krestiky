(ns noliky.BoardTest
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Position :as P]
            [noliky.MoveResult :as M]
            [noliky.Types :as T]
            [clojure.set :refer [intersection]]
            [cemerick.cljs.test :as t :refer-macros (is deftest)]
            ;; [cemerick.double-check.generators :as gen]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer-macros (for-all)]
            [clojure.test.check.clojure-test :refer-macros (defspec)]))

(defprotocol strategy
  (first-move [this]) 
  (next-move [this board]))

(def random-moves
  (reify strategy

    (first-move [this]
      (let [pos (rand-nth P/positions)]
        (M/keepPlaying (B/--> pos (B/empty-board)))))

    (next-move [this board]
      (let [ops (set (BL/occupiedPositions board))
            aps (set P/positions)]
        (rand-nth (seq (intersection ops aps)))))))

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
         sb (M/keepPlaying (next-move random-moves fb))]
     ;; b.occupiedPositions.length == 2 &&
     (= 2 (count (BL/occupiedPositions sb)))
     ;; b.occupiedPositions.head != b.occupiedPositions.last &&
     ;; b.whoseTurn == Player.Player1
     )))
