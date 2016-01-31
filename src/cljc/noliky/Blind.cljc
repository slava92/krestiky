(ns noliky.Blind
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Position :as P]
            [noliky.MoveResult :as M]
            [noliky.Types :as T]
            [clojure.set :refer [difference]]))

(def deep-thought
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (let [pos (rand-nth P/positions)]
        (M/keepPlaying (B/--> pos (B/empty-board)))))

    ;; this -> Board -> Position
    (next-move [this board]
      (let [ops (BL/occupiedPositions board)
            aps (set P/positions)]
        (rand-nth (seq (difference aps ops)))))))
