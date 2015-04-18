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
  Board
  (move-to [board pos]
    (let [new-board (B/->board-type
                     (alternate (BL/whose-turn board))
                     {(to-int pos) (BL/whose-turn board)}
                     (+ (BL/nmoves board) 1)
                     nil)]
      (MR/mk-keep-playing new-board))))

(defmethod BL/empty-board? empty-board-type [_] true)

(defmethod BL/nmoves empty-board-type [_] 0)

(defmethod BL/occupied empty-board-type [_] [])

(defmethod BL/player-at empty-board-type [_ _] nil)

(defmethod BL/whose-turn empty-board-type [_] Plr/Player1)

(def empty-board (->empty-board-type))

;; debugging: ebs is a string representaion of an empty board
(def ebs (BL/as-string empty-board BL/simple-chars))
