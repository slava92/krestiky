(ns krestiky.FinishedBoard
  (:require [krestiky.BoardLike :refer [BoardLike] :as BL]
            [krestiky.Board :as B]
            [krestiky.Position :refer [Position to-int] :as Pos]
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
  (empty? [this] false)
  (nmoves [this] 0)
  (occupied [this] [])
  (player-at [this pos] nil)
  (whose-turn [this] Player1)
  FinishedBoard
  (takeBack [this] (.before board)) ;; it should be board.before.some()
  (result [this] nil))
