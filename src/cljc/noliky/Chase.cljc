(ns noliky.Chase
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.GameSpace :as GS]
            [noliky.MoveResult :as M]
            [noliky.Types :as T]))

(def deep-thought
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (let [board (B/empty-board)
            tag (BL/showBlock board)
            moves (get GS/moves tag)
            pos (rand-nth moves)]
        (M/keepPlaying (B/--> pos board))))

    ;; this -> Board -> Position
    (next-move [this board]
      (let [tag (BL/showBlock board)
            moves (get GS/moves tag)]
        (rand-nth moves)))))
