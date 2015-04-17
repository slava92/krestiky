(ns krestiky.FinishedBoard
  (:require [krestiky.Types :refer :all]
            [krestiky.BoardLike :as BL])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann-record finished-board-type [board :- Board])
(defrecord finished-board-type [board]
  Started
  (take-back [board] (:board board))
  FinishedBoard
  (result [board]
    ;; if (board.gotWinner())
    ;;     return GameResult.win(board.whoseNotTurn());
    ;; else
    ;;     return GameResult.Draw;
    (throw (Exception. "TBI"))))

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
