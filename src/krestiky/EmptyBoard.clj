(ns krestiky.EmptyBoard
  (:require [krestiky.Types :refer :all]
            [krestiky.BoardLike :as BL]
            [krestiky.Board :as B]
            [krestiky.MoveResult :as MR]
            [krestiky.Player :as Plr])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann-record empty-board-type [])
(defrecord empty-board-type []
  Empty
  (start-to [board pos]
    (let [new-board (B/->board-type
                     (alternate (BL/whose-turn board))
                     {(to-int pos) (BL/whose-turn board)}
                     (+ (BL/nmoves board) 1)
                     nil)]
      new-board)))

(defmethod BL/empty-board? empty-board-type [_] true)

(defmethod BL/nmoves empty-board-type [_] 0)

(defmethod BL/occupied empty-board-type [_] [])

(defmethod BL/player-at empty-board-type [_ _] nil)

(defmethod BL/whose-turn empty-board-type [_] Plr/Player1)

(def empty-board (->empty-board-type))
