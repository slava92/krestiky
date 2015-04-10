(ns krestiky.Player
  (:require [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/defprotocol Player
  "A Player"
  (alternate [this :- Player] :- Player
             "Returns player that has the next move.")
  (to-symbol [this :- Player] :- char
            "Returns symbol representing the player.")
  (to-string [this :- Player] :- String
            "Returns string representing the player.")
  (value-of [this :- Player name :- String] :- Player
           "Returns player based on its name"))

(t/ann Player1 Player)
(declare Player1)
(t/ann Player2 Player)
(declare Player2)

(t/ann-datatype player [c :- char s :- String])
(deftype ^:private player [c s]
  Player
  (alternate [this] (if (= this Player1) Player2 Player1))
  (to-symbol [_] c)
  (to-string [_] s)
  (value-of [_ name]
    (match name
           "Alice" Player1
           "Bob" Player2)))

(def Player1 (->player \O "Alice"))
(def Player2 (->player \X "Bob"))

(t/ann values (t/Coll Player))
(def values [Player1 Player2])
