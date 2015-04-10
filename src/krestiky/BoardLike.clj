(ns krestiky.BoardLike
  (:require [krestiky.Position :refer [Position to-int] :as Pos]
            [krestiky.Player :refer [Player alternate] :as Plr]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)
(t/defprotocol [[x :variance :covariant]] P1 (_1 [this :- (P1 x)] :- x))

(t/ann-datatype board-like [])
(deftype board-like [])

(t/defn clazz [x :- t/Any] :- (t/Option java.lang.Class) (class x))
(t/ann empty-board? [t/Any -> boolean])
(defmulti empty-board? clazz)
(defmethod empty-board? board-like [board] (throw (Exception. "abstract")))

(t/ann nmoves [t/Any -> t/AnyInteger])
(defmulti nmoves clazz)
(defmethod nmoves board-like [board] (throw (Exception. "abstract")))

(t/ann occupied [t/Any -> (t/Coll Position)])
(defmulti occupied clazz)
(defmethod occupied board-like [board] (throw (Exception. "abstract")))

(t/ann player-at [t/Any Position -> (t/Option Player)])
(defmulti player-at (t/fn [x :- t/Any y :- Position] (clazz x)))
(defmethod player-at board-like [board pos] (throw (Exception. "abstract")))

(t/ann whose-turn [t/Any -> Player])
(defmulti whose-turn clazz)
(defmethod whose-turn board-like [board] (throw (Exception. "abstract")))

(t/ann whose-not-turn [t/Any -> Player])
(defmulti whose-not-turn clazz)
(defmethod whose-not-turn board-like [board] (alternate (whose-turn board)))

(t/ann occupied? [t/Any Position -> boolean])
(defmulti occupied? (t/fn [x :- t/Any y :- Position] (clazz x)))
(defmethod occupied? board-like [brd pos]
  (not= nil (player-at brd pos)))

(t/ann not-occupied? [t/Any Position -> boolean])
(defmulti not-occupied? (t/fn [x :- t/Any y :- Position] (clazz x)))
(defmethod not-occupied? board-like [brd pos]
  (= nil (player-at brd pos)))

(t/ann player-at-or [t/Any Position (P1 Player) -> Player])
(defmulti player-at-or (t/fn [b :- t/Any x :- Position or :- (P1 Player)] (clazz x)))
(defmethod player-at-or board-like [brd pos or]
  (if-let [plr (player-at brd pos)] plr (_1 or)))

(t/defalias board-fold-fn (t/IFn [(t/Option Player) Position -> char]))
(t/ann to-string [t/Any board-fold-fn -> String])
(defmulti to-string (t/fn [board :- t/Any af :- board-fold-fn] (clazz board)))
(defmethod to-string :default [board af]
  (let [ps (->> Pos/values (sort-by Pos/to-int)
                (map (t/fn [p :- Position] :- char (af (player-at board p) p)))
                (partition-all 3)
                (map (t/fn [cs :- (t/Seqable char)] (interpose \space cs)))
                (interpose ["\n"]))]
    (apply str (apply concat ps))))

(t/ann simple-chars board-fold-fn)
(defn simple-chars [p n]
  (if (nil? p) \_ (Plr/to-symbol p)))

;; (t/defprotocol IBoardLike
;;   "A IBoardLike thing"
;;   (empty-board? [this :-  IBoardLike] :- boolean)
;;   (nmoves [this :- IBoardLike] :- t/AnyInteger)
;;   (occupied [this :- IBoardLike] :- (t/Seqable Position))
;;   (player-at [this :- IBoardLike p :- Position] :- (t/Option Player))
;;   (whose-turn [this :- IBoardLike] :- Player)
;;   (occupied? [tis :- IBoardLike p :- Position] :- boolean)
;;   (not-occupied? [tis :- IBoardLike p :- Position] :- boolean)
;;   (player-at-or [this :- IBoardLike p :- Position or :- (P1 Player)] :- Player)
;;   (whose-not-turn [this :- IBoardLike] :- Player)
;;   (to-string [this :- IBoardLike
;;              af :- (t/IFn [(t/Option Player) Position -> char])] :- String))
;; (deftype BoardLike []
;;   IBoardLike
;;   (empty-board? [this] (throw (Exception. "abstract")))
;;   (nmoves [this] (throw (Exception. "abstract")))
;;   (occupied [this] (throw (Exception. "abstract")))
;;   (player-at [this p] (throw (Exception. "abstract")))
;;   (whose-turn [this] (throw (Exception. "abstract")))
;;   (occupied? [this p]
;;     (not= nil (player-at this p)))
;;   (not-occupied? [this p]
;;     (= nil (player-at this p)))
;;   (whose-not-turn [this]
;;     (alternate (whose-turn this)))
;;   (to-string
;;     [this af]
;;     (let [ps (->> Pos/values (sort-by Pos/to-int)
;;                   (map (t/fn [p :- Position] :- char (af (player-at this p) p)))
;;                   (partition-all 3)
;;                   (map (t/fn [cs :- (t/Seqable char)] (interpose \space cs)))
;;                   (interpose ["\n"]))]
;;       (apply str (concat ps)))))
;; abstract boolean	empty-board?() 
;; abstract int	nmoves() 
;; abstract fj.data.List<Position>	occupied() 
;; abstract fj.data.Option<Player>	player-at(Position p) 
;; abstract Player	whose-turn() 
;; boolean	not-occupied?(Position p) 
;; boolean	occupied?(Position p) 
;; Player	player-at-or(Position p, fj.P1<Player> or) 
;; java.lang.String	to-string(fj.F2<fj.data.Option<Player>,Position,java.lang.Character> f)
;; Player	whose-not-turn() 
