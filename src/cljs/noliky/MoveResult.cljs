(ns noliky.MoveResult
  (:require [noliky.Types :as T]))

(defmulti foldMoveResult
  (fn [occ kp gf mr] (:type mr)))

(defmethod foldMoveResult :PositionOccupied [occ _ _ _] occ)

(defmethod foldMoveResult :KeepPlaying [_ kp _ mr] (kp (:board mr)))

(defmethod foldMoveResult :GameFinished [_ _ gf mr] (gf (:board mr)))

(defn keepPlayingOr [e kp mr] (foldMoveResult e kp (constantly e) mr))

(defn keepPlaying [mr]
  (foldMoveResult nil identity (constantly nil) mr))

(defmethod T/show :PositionOccupied [_] "*Position already occupied*")
(defmethod T/show :KeepPlaying [mr] (str "{" (T/show (:board mr)) "}"))
(defmethod T/show :GameFinished [mr] (str "{{" (T/show (:board mr)) "}}"))
