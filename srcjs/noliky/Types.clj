(ns noliky.Types
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann-record Player [name :- String])
(defrecord Player [name])

(def Player1 (->Player "Alice"))
(def Player2 (->Player "Bob"))
(def Nobody (->Player "Ghost"))

(t/ann-record Position [pos :- String])
(defrecord Position [pos])

(t/ann-record GameResult [result :- t/Kw player :- Player])
(defrecord GameResult [result player])

(t/ann-record EmptyBoard [])
(defrecord EmptyBoard [])

(t/ann-record Board [moves :- (t/List (t/HVec [Position Player]))
                     positions :- (t/Map Position Player)])
(defrecord Board [moves positions])

(t/ann-record FinishedBoard [b :- Board gr :- GameResult])
(defrecord FinishedBoard [b gr])

(t/ann-record UnfinishedEmpty [b :- EmptyBoard])
(defrecord UnfinishedEmpty [b])

(t/ann-record UnfinishedBoard [b :- Board])
(defrecord UnfinishedBoard [b])

(t/defalias Unfinished (t/U UnfinishedEmpty UnfinishedBoard))

(t/ann-record UnemptyBoard [b :- Board])
(defrecord UnemptyBoard [b])

(t/ann-record UnemptyFinished [b :- FinishedBoard])
(defrecord UnemptyFinished [b])

(t/defalias Unempty (t/U UnemptyFinished UnemptyBoard))

(t/ann-record PositionOccupied [])
(defrecord PositionOccupied [])

(t/ann-record KeepPlaying [board :- Board])
(defrecord KeepPlaying [board])

(t/ann-record GameFinished [board :- FinishedBoard])
(defrecord GameFinished [board])

(t/defalias MoveResult (t/U PositionOccupied KeepPlaying GameFinished))

(t/ann-record TakeBackIsEmpty[])
(defrecord TakeBackIsEmpty[])

(t/ann-record TakeBackIsBoard [board :- Board])
(defrecord TakeBackIsBoard [board])

(t/defalias TakenBack (t/U TakeBackIsEmpty TakeBackIsBoard))

(defn abstract [s] (throw (Exception. (format "abstract '%s'" s))))
(defn undefined [] (abstract "TBI"))

(t/defn clazz [x :- t/Any] :- (t/U Class nil) (class x))
(t/ann show [t/Any -> String])
(defmulti show clazz)
(defmethod show :default [x] (abstract "show"))
