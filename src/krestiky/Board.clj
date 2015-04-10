(ns krestiky.Board
  (:require [krestiky.BoardLike :as BL]
            [krestiky.Position :refer [Position to-int] :as Pos]
            [krestiky.Player :refer [Player alternate]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]])
  (:import (clojure.lang APersistentMap)))
(set! *warn-on-reflection* true)
(t/defalias TakenBack t/Any)
(t/defalias MoveResult t/Any)

(t/ann-record board-type
              [next-move :- Player
               pos-map :- (APersistentMap t/AnyInteger Player)
               n-moves :- t/AnyInteger
               before :- (t/Option board-type)])
(defrecord board-type [next-move pos-map n-moves before])

(defmethod BL/empty-board? board-type [board] false)

(defmethod BL/nmoves board-type [{:keys [n-moves]}] n-moves)

(defmethod BL/occupied board-type [{:keys [pos-map]}]
  (t/ann-form pos-map (APersistentMap t/AnyInteger Player))
  (map (t/fn [pos :- t/AnyInteger] :- Position (Pos/from-int pos))
       (keys pos-map)))

(defmethod BL/player-at board-type [{:keys [pos-map]} pos]
          (->> pos Pos/to-int (get pos-map)))

(defmethod BL/whose-turn board-type [{:keys [next-move]}] next-move)

(t/defprotocol Board
  "Implementation specific methods"
  (take-back [this :- Board] :- TakenBack)
  (move-to [this :- Board pos :- Position] :- MoveResult)
  (to-string [this :- Board] :- String))

(extend-type board-type
  Board
  (take-back [board] (throw (Exception. "abstract")))
  (move-to [board pos]
    (t/ann-form board Board)
    (t/ann-form board board-type)
    (t/ann-form pos Position)
    (throw (Exception. "abstract")))
  (to-string [board] (BL/to-string board BL/simple-chars)))
  
;; (t/ann-datatype
;;  board-type [next-move :- Player
;;              pos-map :- (APersistentMap t/AnyInteger Player)
;;              n-moves :- t/AnyInteger
;;              before :- (t/Option board-type)])
;; (deftype board-type [next-move pos-map n-moves before]
;;   BoardLike
;;   (empty-board? [this] false)
;;   (nmoves [this] n-moves)
;;   (occupied [this]
;;     (map (t/fn [pos :- t/AnyInteger] :- Position (Pos/from-int pos))
;;          (keys pos-map)))
;;   (player-at [this pos]
;;     (get pos-map (Pos/to-int pos)))
;;   (whose-turn [this] next-move)
;;   (to-string [this] "")
;;   Board
;;   (take-back [this] nil)
;;   (move-to [this pos] nil))
