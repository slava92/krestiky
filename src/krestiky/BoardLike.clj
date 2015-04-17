(ns krestiky.BoardLike
  (:require [krestiky.Types :refer :all]
            [krestiky.Position :as Pos]
            [krestiky.Player :as Plr])
  (:import [krestiky.Types P1])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann-record board-like [])
(defrecord board-like [])

(t/defn clazz [x :- t/Any] :- (t/Option java.lang.Class) (class x))
(t/ann empty-board? [t/Any -> boolean])
(defmulti empty-board? clazz)
(defmethod empty-board? :default [board] (throw (Exception. "abstract")))

(t/ann nmoves [t/Any -> t/AnyInteger])
(defmulti nmoves clazz)
(defmethod nmoves :default [board] (throw (Exception. "abstract")))

(t/ann occupied [t/Any -> (t/Coll Position)])
(defmulti occupied clazz)
(defmethod occupied :default [board] (throw (Exception. "abstract")))

(t/ann player-at [t/Any Position -> (t/Option Player)])
(defmulti player-at (t/fn [x :- t/Any y :- Position] (clazz x)))
(defmethod player-at :default [board pos] (throw (Exception. "abstract")))

(t/ann whose-turn [t/Any -> Player])
(defmulti whose-turn clazz)
(defmethod whose-turn :default [board] (throw (Exception. "abstract")))

(t/ann whose-not-turn [t/Any -> Player])
(defmulti whose-not-turn clazz)
(defmethod whose-not-turn :default [board] (alternate (whose-turn board)))

(t/ann occupied? [t/Any Position -> boolean])
(defmulti occupied? (t/fn [x :- t/Any y :- Position] (clazz x)))
(defmethod occupied? :default [brd pos]
  (not= nil (player-at brd pos)))

(t/ann not-occupied? [t/Any Position -> boolean])
(defmulti not-occupied? (t/fn [x :- t/Any y :- Position] (clazz x)))
(defmethod not-occupied? :default [brd pos]
  (= nil (player-at brd pos)))

(t/ann player-at-or [t/Any Position (P1 Player) -> Player])
(defmulti player-at-or (t/fn [b :- t/Any x :- Position or :- (P1 Player)] (clazz x)))
(defmethod player-at-or :default [brd pos or]
  (if-let [plr (player-at brd pos)] plr (:_1 or)))

(t/defalias board-fold-fn (t/IFn [(t/Option Player) Position -> char]))
(t/ann as-string [t/Any board-fold-fn -> String])
(defmulti as-string (t/fn [board :- t/Any af :- board-fold-fn] (clazz board)))
(defmethod as-string :default [board af]
  (->> Pos/values (sort-by to-int)
       (map (t/fn [p :- Position] :- char (af (player-at board p) p)))
       (partition-all 3)
       (map (t/fn [cs :- (t/Coll char)] (interpose \space cs)))
       (interpose [\newline])
       (map (t/fn [cs :- (t/Coll char)] (apply str cs)))
       (apply str)))

(t/ann simple-chars board-fold-fn)
(defn simple-chars [p n]
  (if (nil? p) \_ (to-symbol p)))
