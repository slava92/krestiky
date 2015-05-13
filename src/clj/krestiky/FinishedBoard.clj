(ns krestiky.FinishedBoard
  (:require [krestiky.Types :refer :all]
            [krestiky.BoardLike :as BL]
            [krestiky.GameResult :as GR])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann-record finished-board-type [board :- Board])
(defrecord finished-board-type [board]
  FinishedBoard
  (result [board]
    (if (got-winner (:board board))
      (GR/win (BL/whose-not-turn board))
      GR/Draw))
  (fb-take-back [board] (:board board)))

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
