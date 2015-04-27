(ns hrest.MoveResult
  (:require [hrest.Types :refer :all])
  (:import [hrest.Types EmptyBoard Board FinishedBoard
            PositionOccupied KeepPlaying GameFinished])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann foldMoveResult
       (t/All [a] [a ;; ^ The move was to a position that is already occupied by a player.
                   [Board -> a] ;; ^ The move was valid and the board is in a new state.
                   [FinishedBoard -> a] ;; ^ The move was valid and the game is complete.
                   t/Any
                   -> a]))
(defmulti foldMoveResult
  (t/fn [occ :- t/Any kp :- t/Any gf :- t/Any mr :- t/Any] (clazz mr)))

(defmethod foldMoveResult PositionOccupied [occ _ _ _] occ)

(defmethod foldMoveResult KeepPlaying [_ kp _ mr] (kp (:board mr)))

(defmethod foldMoveResult GameFinished [_ _ gf mr] (gf (:board mr)))

(t/ann keepPlayingOr
       (t/All [a] [a ;; ^ The value to return if there is no board to keep playing with.
                   [Board -> a] ;; ^ A function to apply to the board to keep playing with.
                   t/Any ;; MoveResult
                   -> a]))
(defn keepPlayingOr [e kp mr] (foldMoveResult e kp (constantly e) mr))

(t/defn keepPlaying [mr :- t/Any] :- (t/Option Board)
  (foldMoveResult nil (t/ann-form identity [Board -> Board])  (constantly nil) mr))

(defmethod show PositionOccupied [_] "*Position already occupied*")
(defmethod show KeepPlaying [mr] (show (:board mr)))
(defmethod show GameFinished [mr] (show (:board mr)))
