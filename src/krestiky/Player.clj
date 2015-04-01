(ns krestiky.Player
  (:require [clojure.core.match :refer [match]]))

(defprotocol Player
  "A Player"
  (alternate [this] "Returns player that has the next move.")
  (toSymbol [this] "Returns symbol representing the player.")
  (toString [this] "Returns string representing the player."))

(deftype player1 [])
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

(defn valueOf [name]
  (match name
         "A" Player1
         "B" Player2
         :else (throw (IllegalArgumentException.
                       (format "illegal player name %s" name)))))

(defn values [] [Player1 Player2])
