(ns hrest.Board
  (:require [hrest.Types :refer :all])
  (:import [hrest.Types EmptyBoard Board FinishedBoard
            PositionOccupied KeepPlaying GameFinished])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(defmethod show EmptyBoard [eb]
  ".=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=. [ Player 1 to move ]")

(t/ann --> (t/All [from] [Position from -> MoveResult]))
(defmulti --> (fn [pos board] (clazz board)))

(defmethod --> EmptyBoard [pos from]
  (->KeepPlaying (->Board [[pos Player1]] {pos Player1})))

(defmethod --> Board [pos {:keys [moves poss] :as bd}]
  (throw (Exception. "TBI")))
