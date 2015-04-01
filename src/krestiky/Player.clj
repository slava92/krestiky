(ns krestiky.Player
  (:require [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))

(t/defprotocol Player
  "A Player"
  (alternate [this] "Returns player that has the next move.")
  (toSymbol [this] "Returns symbol representing the player.")
  (toString [this] "Returns string representing the player."))

(t/ann-datatype player1 [])
(deftype player1 [])

(t/ann-datatype player2 [])
(deftype player2 [])

(def Player1 (player1.))
(def Player2 (player2.))

(extend-protocol Player
  player1
  (alternate [this] Player2)
  (toSymbol [this] \A)
  (toString [this] "A")

  player2
  (alternate [this] Player1)
  (toSymbol [this] \B)
  (toString [this] "B"))

(t/ann valueOf [String -> (t/U player1 player2)])
(defn valueOf [name]
  (match name
         "A" Player1
         "B" Player2))

(defn values [] [Player1 Player2])
