(ns krestiky.Player
  (:require [krestiky.Types :refer :all]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann Player1 Player)
(declare Player1)
(t/ann Player2 Player)
(declare Player2)

(t/ann-record player [c :- char s :- String])
(defrecord ^:private player [c s]
  Player
  (alternate [this] (if (= this Player1) Player2 Player1))
  (to-symbol [_] c)
  Show
  (to-string [_] s))

(t/defn value-of [name :- String] :- Player
  (match name
         "Alice" Player1
         "Bob" Player2))

(def Player1 (->player \O "Alice"))
(def Player2 (->player \X "Bob"))

(t/ann values (t/Coll Player))
(def values [Player1 Player2])
