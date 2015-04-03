(ns krestiky.BoardLike
  (:require [krestiky.Position :refer [Position]]
   [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))

(t/defprotocol BoardLike
  "A BoardLike thing"
  (isEmpty [this :-  BoardLike] :- boolean)
  (isOccupied [this :- BoardLike p :- Position] :- boolean)
  (isNotOccupied [this :- BoardLike p :- Position] :- boolean)
  (nmoves [this :- BoardLike] :- int)
  (playerAt [this :- BoardLike p :- Position] :- (t/Option Player))
  )

;; abstract boolean	isEmpty() 
;; boolean	isNotOccupied(Position p) 
;; boolean	isOccupied(Position p) 
;; abstract int	nmoves() 
;; abstract fj.data.List<Position>	occupiedPositions() 
;; abstract fj.data.Option<Player>	playerAt(Position p) 
;; Player	playerAtOr(Position p, fj.P1<Player> or) 
;; java.lang.String	toString(fj.F2<fj.data.Option<Player>,Position,java.lang.Character> f) 
;; Player	whoseNotTurn() 
;; abstract Player
