(ns noliky.MoveResult
  (:require [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

(defmulti foldMoveResult
  (fn [occ kp gf mr] (:type mr)))

(s/defmethod ^:always-validate foldMoveResult :PositionOccupied :- s/Any
  [occ :- s/Any _ _ _] occ)

(s/defmethod ^:always-validate foldMoveResult :KeepPlaying :- s/Any
  [_ kp :- (s/=> T/BoardType s/Any) _ mr :- T/KeepPlayingType]
  (kp (:board mr)))

(s/defmethod ^:always-validate foldMoveResult :GameFinished :- s/Any
  [_ _ gf :- (s/=> T/FinishedBoardType s/Any) mr :- T/GameFinishedType]
  (gf (:board mr)))

(s/defn keepPlayingOr :- s/Any
  [e :- s/Any
   kp :- (s/=> T/BoardType s/Any)
   mr :- T/GameFinishedType]
  (foldMoveResult e kp (constantly e) mr))

;;; !!! Union types here
(s/defn keepPlaying [mr]
  (foldMoveResult nil identity (constantly nil) mr))

(defmethod T/show :PositionOccupied [_] "*Position already occupied*")
(defmethod T/show :KeepPlaying [mr] (str "{" (T/show (:board mr)) "}"))
(defmethod T/show :GameFinished [mr] (str "{{" (T/show (:board mr)) "}}"))
