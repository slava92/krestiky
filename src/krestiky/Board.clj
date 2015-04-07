(ns krestiky.Board
  (:require [krestiky.BoardLike :refer [BoardLike]]
            [krestiky.Position :refer [Position toInt] :as Pos]
            [krestiky.Player :refer [Player alternate]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t])
  (:import (clojure.lang APersistentMap)))
(set! *warn-on-reflection* true)

(t/defalias Board board-type)
(t/ann-datatype board-type
                [nextMove :- Player
                 posMap :- (APersistentMap t/AnyInteger Player)
                 nMoves :- t/AnyInteger
                 before :- (t/Option Board)])
(t/defalias TakenBack t/Any)
(t/defalias MoveResult t/Any)
(t/defprotocol BoardImpl
  "Implementation specific methods"
  (takeBack [this :- BoardImpl] :- TakenBack)
  (moveTo [this :- BoardImpl pos :- Position] :- MoveResult))

(deftype board-type [nextMove posMap nMoves before]
  BoardLike
  (isEmpty [this] false)
  (nmoves [this] nMoves)
  (occupiedPositions [this]
    (map (t/fn [pos :- t/AnyInteger] :- Position (Pos/fromInt pos))
         (keys posMap)))
  (playerAt [this pos]
    (get posMap (Pos/toInt pos)))
  (whoseTurn [this] nextMove)
  (toString [this] "")
  BoardImpl
  (takeBack [this] nil)
  (moveTo [this pos] nil))
