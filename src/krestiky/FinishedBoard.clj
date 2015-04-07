(ns krestiky.FinishedBoard
  (:require [krestiky.BoardLike :refer [BoardLike] :as BL]
            [krestiky.Board :as B]
            [krestiky.Position :refer [Position toInt] :as Pos]
            [krestiky.Player :refer [Player Player1 alternate]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))
(set! *warn-on-reflection* true)
(t/defalias MoveResult t/Any)
(t/defalias GameResult t/Any)

(t/defprotocol FinishedBoard
  "Implementation specific methods"
  (takeBack [this :- FinishedBoard] :- B/board-type)
  (result [this :- FinishedBoard] :- GameResult))

(t/ann-datatype FinishedBoardT [board :- B/board-type])
(deftype FinishedBoardT [board]
  BoardLike
  (isEmpty [this] true)
  (nmoves [this] 0)
  (occupiedPositions [this] [])
  (playerAt [this pos] nil)
  (whoseTurn [this] Player1)
  FinishedBoard
  (takeBack [this] (.before board)) ;; it should be board.before.some()
  (result [this] nil))
