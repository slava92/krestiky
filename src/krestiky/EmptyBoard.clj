(ns krestiky.EmptyBoard
  (:require [krestiky.BoardLike :as BL]
            [krestiky.Board :as B]
            [krestiky.Position :refer [Position] :as Pos]
            [krestiky.Player :refer [Player Player1 alternate]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)
(t/defalias MoveResult t/Any)

(t/ann-record empty-board-type [])
(defrecord empty-board-type [])

(defmethod BL/empty-board? empty-board-type [_] true)

(defmethod BL/nmoves empty-board-type [_] 0)

(defmethod BL/occupied empty-board-type [_] [])

(defmethod BL/player-at empty-board-type [_ _] nil)

(defmethod BL/whose-turn empty-board-type [_] Player1)

(t/defprotocol EmptyBoard
  "Implementation specific methods"
  (move-to [board :- EmptyBoard pos :- Position] :- B/Board))

(extend-type empty-board-type
  EmptyBoard
  (move-to [board pos]
    (B/->board-type (alternate (BL/whose-turn board))
                    {(Pos/to-int pos) (BL/whose-turn board)}
                    (+ (BL/nmoves board) 1)
                    nil)))

(def empty-board (->empty-board-type))

;; debugging: ebs is a string representaion of an empty board
(def ebs (BL/to-string empty-board BL/simple-chars))
