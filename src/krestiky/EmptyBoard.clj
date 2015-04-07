(ns krestiky.EmptyBoard
  (:require [krestiky.BoardLike :refer [BoardLike] :as BL]
            [krestiky.Board :refer [Board] :as B]
            [krestiky.Position :refer [Position toInt] :as Pos]
            [krestiky.Player :refer [Player Player1 alternate]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))
(set! *warn-on-reflection* true)

(t/defalias MoveResult t/Any)
(t/defprotocol EmptyBoard
  "Implementation specific methods"
  (moveTo [this :- EmptyBoard pos :- Position] :- B/Board))

(t/ann-datatype empty-board-type [])
(deftype empty-board-type [])

(extend-type empty-board-type
  BoardLike
  (isEmpty [this] true)
  (nmoves [this] 0)
  (occupiedPositions [this] [])
  (playerAt [this pos] nil)
  (whoseTurn [this] Player1)
  EmptyBoard
  (moveTo [this pos] (B/->board-type (alternate (BL/whoseTurn this))
                                     {(toInt pos) (BL/whoseTurn this)}
                                     (+ (BL/nmoves this) 1)
                                     nil)))
  
(t/defn empty-board [] :- EmptyBoard (->empty-board-type))
