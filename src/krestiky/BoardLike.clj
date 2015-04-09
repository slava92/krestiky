(ns krestiky.BoardLike
  (:require [krestiky.Position :refer [Position toInt] :as Pos]
            [krestiky.Player :refer [Player alternate]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)
(t/defprotocol [[x :variance :covariant]] P1 (_1 [this :- (P1 x)] :- x))

(t/ann-datatype board-like [])
(deftype board-like [])

(t/ann isEmpty [t/Any -> boolean])
(defmulti isEmpty type)
(defmethod isEmpty :default [_] (throw (Exception. "abstract")))
(t/ann nmoves [t/Any -> t/AnyInteger])
(defmulti nmoves type)
(defmethod nmoves :default [_] (throw (Exception. "abstract")))
(t/ann occupiedPositions [t/Any -> (t/Coll Position)])
(defmulti occupiedPositions type)
(defmethod occupiedPositions :default [_] (throw (Exception. "abstract")))
(t/ann playerAt [t/Any Position -> (t/Option Player)])
(defmulti playerAt (t/fn [x :- t/Any y :- Position] (type x)))
(defmethod playerAt :default [_ _] (throw (Exception. "abstract")))
(t/ann whoseTurn [t/Any -> Player])
(defmulti whoseTurn type)
(defmethod whoseTurn :default [_] (throw (Exception. "abstract")))

;; (t/defprotocol IBoardLike
;;   "A IBoardLike thing"
;;   (isEmpty [this :-  IBoardLike] :- boolean)
;;   (nmoves [this :- IBoardLike] :- t/AnyInteger)
;;   (occupiedPositions [this :- IBoardLike] :- (t/Seqable Position))
;;   (playerAt [this :- IBoardLike p :- Position] :- (t/Option Player))
;;   (whoseTurn [this :- IBoardLike] :- Player)
;;   (isOccupied [tis :- IBoardLike p :- Position] :- boolean)
;;   (isNotOccupied [tis :- IBoardLike p :- Position] :- boolean)
;;   (playerAtOr [this :- IBoardLike p :- Position or :- (P1 Player)] :- Player)
;;   (whoseNotTurn [this :- IBoardLike] :- Player)
;;   (toString [this :- IBoardLike
;;              af :- (t/IFn [(t/Option Player) Position -> char])] :- String))

;; (deftype BoardLike []
;;   IBoardLike
;;   (isEmpty [this] (throw (Exception. "abstract")))
;;   (nmoves [this] (throw (Exception. "abstract")))
;;   (occupiedPositions [this] (throw (Exception. "abstract")))
;;   (playerAt [this p] (throw (Exception. "abstract")))
;;   (whoseTurn [this] (throw (Exception. "abstract")))
;;   (isOccupied [this p]
;;     (not= nil (playerAt this p)))
;;   (isNotOccupied [this p]
;;     (= nil (playerAt this p)))
;;   (whoseNotTurn [this]
;;     (alternate (whoseTurn this)))
;;   (toString
;;     [this af]
;;     (let [ps (->> Pos/values (sort-by Pos/toInt)
;;                   (map (t/fn [p :- Position] :- char (af (playerAt this p) p)))
;;                   (partition-all 3)
;;                   (map (t/fn [cs :- (t/Seqable char)] (interpose \space cs)))
;;                   (interpose ["\n"]))]
;;       (apply str (concat ps)))))

;; abstract boolean	isEmpty() 
;; abstract int	nmoves() 
;; abstract fj.data.List<Position>	occupiedPositions() 
;; abstract fj.data.Option<Player>	playerAt(Position p) 
;; abstract Player	whoseTurn() 
;; boolean	isNotOccupied(Position p) 
;; boolean	isOccupied(Position p) 
;; Player	playerAtOr(Position p, fj.P1<Player> or) 
;; java.lang.String	toString(fj.F2<fj.data.Option<Player>,Position,java.lang.Character> f)
;; Player	whoseNotTurn() 
