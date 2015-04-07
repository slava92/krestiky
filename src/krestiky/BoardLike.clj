(ns krestiky.BoardLike
  (:require [krestiky.Position :refer [Position toInt] :as Pos]
            [krestiky.Player :refer [Player alternate]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))
(set! *warn-on-reflection* true)
(t/defprotocol [[x :variance :covariant]] P1 (_1 [this :- (P1 x)] :- x))

;; abstract boolean	isEmpty() 
;; abstract int	nmoves() 
;; abstract fj.data.List<Position>	occupiedPositions() 
;; abstract fj.data.Option<Player>	playerAt(Position p) 
;; abstract Player	whoseTurn() 
(t/defprotocol BoardLike
  "A BoardLike thing"
  (isEmpty [this :-  BoardLike] :- boolean)
  (nmoves [this :- BoardLike] :- t/AnyInteger)
  (occupiedPositions [this :- BoardLike] :- (t/Seqable Position))
  (playerAt [this :- BoardLike p :- Position] :- (t/Option Player))
  (whoseTurn [this :- BoardLike] :- Player))

(t/defn isNotOccupied [this :- BoardLike p :- Position] :- boolean
  (= nil (playerAt this p)))

(t/defn isOccupied [this :- BoardLike p :- Position] :- boolean
  (not= nil (playerAt this p)))

(t/defn playerAtOr [this :- BoardLike p :- Position or :- (P1 Player)] :- Player
  (let [player (playerAt this p)]
    (if (= nil player) (_1 or) player)))

(t/defn whoseNotTurn [this :- BoardLike] :- Player
  (alternate (whoseTurn this)))

(t/ann clojure.core/sort-by (t/All [a b] [[a -> b] (t/Seqable a) -> (t/Seqable a)]))
(t/defn toString
  [this :- BoardLike
   af :- (t/IFn [(t/Option Player) Position -> char])] :- String
   (let [ps (->> Pos/values (sort-by Pos/toInt)
                 (map (t/fn [p :- Position] :- char (af (playerAt this p) p)))
                 (partition-all 3)
                 (map (t/fn [cs :- (t/Seqable char)] (interpose \space cs)))
                 (interpose ["\n"]))]
     (apply str (concat ps))))

;; boolean	isNotOccupied(Position p) 
;; boolean	isOccupied(Position p) 
;; Player	playerAtOr(Position p, fj.P1<Player> or) 
;; java.lang.String	toString(fj.F2<fj.data.Option<Player>,Position,java.lang.Character> f)
;; Player	whoseNotTurn() 
