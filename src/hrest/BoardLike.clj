(ns hrest.BoardLike
  (:require [hrest.Types :refer :all]
            [hrest.Player :as Plr])
  (:import [hrest.Types EmptyBoard Board FinishedBoard Player Position])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

;; | Returns whose turn it is on a tic-tac-toe board.
(t/ann whoseTurn (t/All [b] [b -> Player]))
(defmulti whoseTurn (t/fn [b :- t/Any] (clazz b)))
(defmethod whoseTurn :default [b] (abstract "whoseTurn"))

;; | Returns whose turn it is not on a tic-tac-toe board.
(t/ann whoseNotTurn (t/All [b] [b -> Player]))
(defmulti whoseNotTurn (t/fn [b :- t/Any] (clazz b)))
(defmethod whoseNotTurn :default [b] (Plr/alternate (whoseTurn b)))

;; | Returns whether or not the board is empty.
(t/ann isEmpty (t/All [b] [b -> boolean]))
(defmulti isEmpty (t/fn [b :- t/Any] (clazz b)))
(defmethod isEmpty :default [b] (abstract "isEmpty"))

;; | Returns positions that are occupied.
(t/ann occupiedPositions (t/All [b] [b -> (t/Set Position)]))
(defmulti occupiedPositions (t/fn [b :- t/Any] (clazz b)))
(defmethod occupiedPositions :default [b] (abstract "occupiedPositions"))

;; | Returns the number of moves that have been played.
(t/ann moves (t/All [b] [b -> t/Num]))
(defmulti moves (t/fn [b :- t/Any] (clazz b)))
(defmethod moves :default [b] (abstract "moves"))

;; | Returns whether or not the first given board can transition to the second given board.
(t/ann isSubboardOf (t/All [b] [b b -> boolean]))
(defmulti isSubboardOf (t/fn [b1 :- t/Any b2 :- t/Any] (clazz b1)))
(defmethod isSubboardOf :default [b1 b2] (abstract "isSubboardOf"))

;; | Returns whether or not the first given board can transition to the second given board and they are inequal.
(t/ann isProperSubboardOf (t/All [b] [b b -> boolean]))
(defmulti isProperSubboardOf (t/fn [b1 :- t/Any b2 :- t/Any] (clazz b1)))
(defmethod isProperSubboardOf :default [b1 b2] (abstract "isProperSubboardOf"))

;; | Returns the player at the given position.
(t/ann playerAt (t/All [b] [b Position -> (t/Option Player)]))
(defmulti playerAt (t/fn [b :- t/Any p :- Position] (clazz b)))
(defmethod playerAt :default [b p] (abstract "playerAt"))

;; | Returns the player at the given position or the given default.
(t/ann playerAtOr (t/All [b] [b Position Player -> Player]))
(defmulti playerAtOr (t/fn [b :- t/Any pos :- Position plr :- Player] (clazz b)))
(defmethod playerAtOr :default [b pos plr] 
  (if-let [player (playerAt b pos)] player plr))

;; | Returns whether or not the given position is occupied on the board. @true@ if occupied.
(t/ann isOccupied (t/All [b] [b Position -> boolean]))
(defmulti isOccupied (t/fn [b :- t/Any p :- Position] (clazz b)))
(defmethod isOccupied :default [b p] (not= nil (playerAt b p)))

;; | Returns whether or not the given position is occupied on the board. @false@ if occupied.
(t/ann isNotOccupied (t/All [b] [b Position -> boolean]))
(defmulti isNotOccupied (t/fn [b :- t/Any p :- Position] (clazz b)))
(defmethod isNotOccupied :default [b p] (not (isOccupied b p)))

;; | Show the board using an ASCII grid representation.
(t/ann showBoard (t/All [b] [b -> String]))
(defmulti showBoard (t/fn [b :- t/Any] (clazz b)))
(defmethod showBoard :default [b] (abstract "showBoard"))

;; | Show the board using a single line.
(t/ann showLine (t/All [b] [b -> String]))
(defmulti showLine (t/fn [b :- t/Any] (clazz b)))
(defmethod showLine :default [b] (abstract "showLine"))
