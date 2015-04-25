(ns hrest.Board
  (:require [hrest.Types :refer :all]
            [hrest.GameResult :as GR]
            [hrest.Player :as Plr]
            [hrest.Position :as Pos])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann EmptyBoard (t/Val ::EmptyBoard))
(def EmptyBoard ::EmptyBoard)

(t/ann-record board [moves :- (t/Coll (t/HVec [Pos/Position Plr/Player]))
                     poss :- (t/Map Pos/Position Plr/Player)])
(defrecord board [moves poss])

(t/ann-record finished-board [b :- board gr :- GR/GameResult])
(defrecord finished-board [b gr])

(defmethod show EmptyBoard [eb]
  ".=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=. [ Player 1 to move ]")

(t/ann --> (t/All [from to] [Pos/Position from -> to]))
(defmulti --> (fn [pos board] (clazz board)))
(defmethod --> [EmptyBoard] [pos from]
  (->board [[pos Plr/Player1]] {pos Plr/Player1}))
