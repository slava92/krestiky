(ns hrest.TakeBack
  (:require [hrest.Types :refer :all]
            [hrest.BoardLike :as BL]
            [hrest.GameResult :as GR]
            [hrest.MoveResult :as MR]
            [hrest.Position :as Pos :refer [NW N NE E SE S SW W C]])
  (:import [hrest.Types EmptyBoard Board FinishedBoard
            PositionOccupied KeepPlaying GameFinished]
           [hrest.Types Position Player])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

;; class TakeBack to from | to -> from where
;;   takeBack :: to -> from
(t/ann takeBack (t/All [from] [from -> TakenBack]))
(defmulti takeBack (fn [board] (clazz board)))

;; instance TakeBack FinishedBoard Board where
;;   takeBack (FinishedBoard (Board ((p, _):t) m) _) =
;;     Board t (p `M.delete` m)
;;   takeBack (FinishedBoard (Board [] _) _) =
;;     error "Broken invariant: board-in-play with empty move list. This is a program bug."
(defmethod takeBack FinishedBoard [{:keys [b]}]
  (let [pos-plr (first (:moves b))
        pos (t/ann-form (if (nil? pos-plr)
                          (abstract "empty moves list - bad design")
                          (first pos-plr))
                        Position)
        positions' (dissoc (:positions b) pos)
        moves' (apply list (rest (:moves b)))]
    (->TakeBackIsBoard (->Board moves' positions'))))
