(ns noliky.BoardLike
  (:require [noliky.Types :as T]
            [noliky.Player :as Plr]))
  ;; (:import [noliky.Types EmptyBoard Board FinishedBoard Player Position])

;; | Returns whose turn it is on a tic-tac-toe board.
(defmulti whoseTurn (fn [b] (:type b)))
(defmethod whoseTurn :default [b] (T/abstract "whoseTurn"))

;; | Returns whose turn it is not on a tic-tac-toe board.
(defmulti whoseNotTurn (fn [b] (:type b)))
(defmethod whoseNotTurn :default [b] (Plr/alternate (whoseTurn b)))

;; | Returns whether or not the board is empty.
(defmulti isEmpty (fn [b] (:type b)))
(defmethod isEmpty :default [b] (T/abstract "isEmpty"))

;; | Returns positions that are occupied.
(defmulti occupiedPositions (fn [b] (:type b)))
(defmethod occupiedPositions :default [b] (T/abstract "occupiedPositions"))

;; | Returns the number of moves that have been played.
(defmulti moves (fn [b] (:type b)))
(defmethod moves :default [b] (T/abstract "moves"))

;; | Returns whether or not the first given board can transition to the second given board.
(defmulti isSubboardOf (fn [b1 b2] [(:type b1) (:type b2)]))
(defmethod isSubboardOf :default [b1 b2] (T/abstract "isSubboardOf"))

;; | Returns whether or not the first given board can transition to the second given board and they are inequal.
(defmulti isProperSubboardOf (fn [b1 b2] [(:type b1) (:type b2)]))
(defmethod isProperSubboardOf :default [b1 b2] (T/abstract "isProperSubboardOf"))

;; | Returns the player at the given position.
(defmulti playerAt (fn [b p] (:type b)))
(defmethod playerAt :default [b p] (T/abstract "playerAt"))

;; | Returns the player at the given position or the given default.
(defmulti playerAtOr (fn [b pos plr] (:type b)))
(defmethod playerAtOr :default [b pos plr] 
  (if-let [player (playerAt b pos)] player plr))

;; | Returns whether or not the given position is occupied on the board. @true@ if occupied.
(defmulti isOccupied (fn [b p] (:type b)))
(defmethod isOccupied :default [b p] (not= nil (playerAt b p)))

;; | Returns whether or not the given position is occupied on the board. @false@ if occupied.
(defmulti isNotOccupied (fn [b p] (:type b)))
(defmethod isNotOccupied :default [b p] (not (isOccupied b p)))

;; | Show the board using an ASCII grid representation.
(defmulti showBoard (fn [b] (:type b)))
(defmethod showBoard :default [b] (T/abstract "showBoard"))

;; | Show the board using a single line.
(defmulti showLine (fn [b] (:type b)))
(defmethod showLine :default [b] (T/abstract "showLine"))
