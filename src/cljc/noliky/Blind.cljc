(ns noliky.Blind
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Position :as P]
            [noliky.MoveResult :as M]
            [noliky.Types :as T]
            [clojure.set :refer [difference]]))

(def random-moves
  (reify T/strategy

    (first-move [this]
      (let [pos (rand-nth P/positions)]
        (M/keepPlaying (B/--> pos (B/empty-board)))))

    (next-move [this board]
      (let [ops (set (BL/occupiedPositions board))
            aps (set P/positions)]
        (rand-nth (seq (difference aps ops)))))))
