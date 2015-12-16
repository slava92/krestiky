(ns noliky.MoveResult
  (:require [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

(s/defn position-occupied :- T/PositionOccupiedType []
  (T/->PositionOccupied :PositionOccupied))

(s/defn keep-playing :- T/KeepPlayingType
  [board :- T/BoardType]
  (T/->KeepPlaying board :KeepPlaying))

(s/defn game-finished :- T/GameFinishedType
  [board :- T/FinishedBoardType]
  (T/->GameFinished board :GameFinished))

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

(s/defn keepPlaying :- s/Any [mr :- T/MoveResultType]
  (foldMoveResult nil identity (constantly nil) mr))

(s/defmethod ^:always-validate T/show :PositionOccupied :- s/Str
  [_]
  "*Position already occupied*")

(s/defmethod ^:always-validate T/show :KeepPlaying :- s/Str
  [mr :- T/KeepPlayingType]
  (str "{" (T/show (:board mr)) "}"))

(s/defmethod ^:always-validate T/show :GameFinished :- s/Str
  [mr :- T/GameFinishedType]
  (str "{{" (T/show (:board mr)) "}}"))
