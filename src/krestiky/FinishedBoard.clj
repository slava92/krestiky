(ns krestiky.FinishedBoard
  (:require [krestiky.BoardLike :as BL]
            [krestiky.Board :as B]
            [krestiky.GameResult :refer [GameResult]]
            [krestiky.Position :refer [Position to-int] :as Pos]
            [krestiky.Player :refer [Player Player1 alternate]]
            [krestiky.MoveResult :refer [MoveResult]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann-record finished-board-type [board :- B/Board])
(defrecord finished-board-type [board])

(defmethod BL/empty-board? finished-board-type [board]
  (BL/empty-board? (:board board)))

(defmethod BL/nmoves finished-board-type [board]
  (BL/nmoves (:board board)))

(defmethod BL/occupied finished-board-type [board]
  (BL/occupied (:board board)))

(defmethod BL/player-at finished-board-type [board pos]
  (BL/player-at (:board board) pos))

(defmethod BL/whose-turn finished-board-type [board]
  (BL/whose-turn (:board board)))

(extend-type finished-board-type
  FinishedBoard
  (take-back [board] (:board board))
  (result [board] (throw (Exception. "TBI"))))
