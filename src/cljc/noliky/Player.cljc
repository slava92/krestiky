(ns noliky.Player
  (:require  #?(:clj [schema.core :as s]
                :cljs [schema.core :as s :include-macros true])
             [noliky.Types :as T]))

(defn player
  [name]
  (T/->Player name :Player))

(def Player1 (player "Alice"))
(def Player2 (player "Bob"))
(def Nobody (player "Ghost"))

(defn fold-player
  [a
   b
   p]
  (if (= p Player1) a b))

(defn isPlayer1
  [p]
  (fold-player true false p))

(defn isPlayer2
  [p]
  (fold-player false true p))

(defn player1
  [] Player1)

(defn player2
  [] Player2)

(defn alternate
  [p]
  (fold-player Player2 Player1 p))

(defn toSymbol
  [p]
  ({Player1 "X"
    Player2 "O"
    Nobody "."} p))

;; use s/Any for char since it is not in schema
(defn from-symbol
  [s]
  ({\X  Player1, \O  Player2, \.  Nobody,
    "X" Player1, "O" Player2, "." Nobody,} s))

(defmethod T/show :Player
  [player]
  (:name player))
