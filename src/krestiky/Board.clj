(ns krestiky.Board
  (:require [krestiky.BoardLike :as BL]
            [krestiky.Position :refer [Position toInt] :as Pos]
            [krestiky.Player :refer [Player alternate]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]])
  (:import (clojure.lang APersistentMap)))
(set! *warn-on-reflection* true)
(t/defalias TakenBack t/Any)
(t/defalias MoveResult t/Any)

(defmethod BL/isEmpty (type "") [_] false)

;; (t/defprotocol Board
;;   "Implementation specific methods"
;;   (takeBack [this :- Board] :- TakenBack)
;;   (moveTo [this :- Board pos :- Position] :- MoveResult))

;; (t/ann-datatype
;;  board-type [nextMove :- Player
;;              posMap :- (APersistentMap t/AnyInteger Player)
;;              nMoves :- t/AnyInteger
;;              before :- (t/Option board-type)])
;; (deftype board-type [nextMove posMap nMoves before]
;;   BoardLike
;;   (isEmpty [this] false)
;;   (nmoves [this] nMoves)
;;   (occupiedPositions [this]
;;     (map (t/fn [pos :- t/AnyInteger] :- Position (Pos/fromInt pos))
;;          (keys posMap)))
;;   (playerAt [this pos]
;;     (get posMap (Pos/toInt pos)))
;;   (whoseTurn [this] nextMove)
;;   (toString [this] "")
;;   Board
;;   (takeBack [this] nil)
;;   (moveTo [this pos] nil))
